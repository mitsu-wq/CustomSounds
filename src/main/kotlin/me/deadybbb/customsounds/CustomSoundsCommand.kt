package me.deadybbb.customsounds

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import me.deadybbb.customsounds.voicechathook.AudioPosition
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player
import java.nio.file.Files
import kotlin.io.path.extension

class CustomSoundsCommand(private val plugin: CustomSounds) {
    companion object {
        private const val DEFAULT_VOLUME = 1.0
        private const val DEFAULT_DISTANCE = 64.0
        private const val MUSICDATA_FOLDER = "musicdata"
    }

    fun registerCommands() {
        CommandAPICommand("customsounds")
            .withSubcommands(
                CommandAPICommand("play")
                    .withArguments(
                        StringArgument("filename").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                            val musicFolder = plugin.dataFolder.toPath().resolve(MUSICDATA_FOLDER)
                            Files.list(musicFolder)
                                .filter { it.extension == "wav" || it.extension == "mp3" }
                                .map { it.fileName.toString() }
                                .toList()
                                .toTypedArray()
                        }),
                        PlayerArgument("player"),
                        DoubleArgument("volume").setOptional(true),
                        DoubleArgument("distance").setOptional(true)
                    )
                    .withPermission("customsounds.play")
                    .executes(CommandExecutor { sender, args ->
                        val filename = args.get("filename") as String
                        val targetPlayer = args.get("player") as Player
                        val volume = args.get("volume") as? Double ?: DEFAULT_VOLUME
                        val distance = args.get("distance") as? Double ?: DEFAULT_DISTANCE

                        try {
                            val position = AudioPosition(
                                x = targetPlayer.location.x,
                                y = targetPlayer.location.y,
                                z = targetPlayer.location.z,
                                world = plugin.hook.handler.api.fromServerLevel(targetPlayer.world)
                            )

                            plugin.hook.playAudio(
                                fileName = filename,
                                player = plugin.hook.handler.api.fromServerPlayer(targetPlayer),
                                position = position,
                                volume = volume,
                                distance = distance,
                                use3D = false
                            )
                            plugin.formatter.sendFormattedMessage(sender,"<green>Воспроизведение статического звука '$filename' для ${targetPlayer.name}")
                        } catch (e: Exception) {
                            plugin.formatter.sendFormattedMessage(sender, "<red>Ошибка воспроизведения: ${e.message}")
                        }
                    }),
                CommandAPICommand("play3d")
                    .withArguments(
                        StringArgument("filename").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                            val musicFolder = plugin.dataFolder.toPath().resolve(MUSICDATA_FOLDER)
                            Files.list(musicFolder)
                                .filter { it.extension == "wav" || it.extension == "mp3" }
                                .map { it.fileName.toString() }
                                .toList()
                                .toTypedArray()
                        }),
                        DoubleArgument("x"),
                        DoubleArgument("y"),
                        DoubleArgument("z"),
                        DoubleArgument("volume").setOptional(true),
                        DoubleArgument("distance").setOptional(true)
                    )
                    .withPermission("customsounds.play3d")
                    .executes (CommandExecutor { sender, args ->
                        val filename = args.get("filename") as String
                        val x = args.get("x") as Double
                        val y = args.get("y") as Double
                        val z = args.get("z") as Double
                        val volume = args.get("volume") as? Double ?: DEFAULT_VOLUME
                        val distance = args.get("distance") as? Double ?: DEFAULT_DISTANCE

                        try {
                            // Используем мир отправителя команды или любой онлайн-игрока, так как мир нужен для ServerLevel
                            val world = (sender as? Player)?.world
                                ?: plugin.server.onlinePlayers.firstOrNull()?.world
                                ?: throw IllegalStateException("Не удалось определить мир для воспроизведения")
                            val position = AudioPosition(
                                x = x,
                                y = y,
                                z = z,
                                world = plugin.hook.handler.api.fromServerLevel(world)
                            )

                            plugin.hook.playAudio(
                                fileName = filename,
                                player = null, // 3D-звук не привязан к игроку
                                position = position,
                                volume = volume,
                                distance = distance,
                                use3D = true
                            )
                            plugin.formatter.sendFormattedMessage(sender,"<green>Воспроизведение 3D звука '$filename' в [$x, $y, $z]")
                        } catch (e: Exception) {
                            plugin.formatter.sendFormattedMessage(sender,"Ошибка воспроизведения: ${e.message}")
                        }
                    }),
                CommandAPICommand("stop")
                    .withPermission("customsounds.stop")
                    .executes(CommandExecutor{ sender, _ ->
                        try {
                            plugin.hook.stopAll()
                            sender.sendMessage("Все звуки остановлены")
                        } catch (e: Exception) {
                            sender.sendMessage("Ошибка при остановке звуков: ${e.message}")
                        }
                    })
            )
            .register()
    }
}