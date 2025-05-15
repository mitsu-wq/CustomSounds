package me.deadybbb.customsounds.voicechathook

import de.maxhenkel.voicechat.api.ServerPlayer
import de.maxhenkel.voicechat.api.VoicechatApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import me.deadybbb.customsounds.base.ServerAdapter
import me.deadybbb.customsounds.voicechathook.handlers.VoicechatAPIHandler
import java.io.IOException
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.exists
import kotlin.io.path.extension

class VoicechatHook(private val adapter: ServerAdapter) : VoicechatPlugin {
    lateinit var handler: VoicechatAPIHandler
    private val MUSICDATA_FOLDER = "musicdata"
    private val playerInstances = ConcurrentHashMap<UUID, AudioPlayerInstance>()

    override fun getPluginId(): String {
        return "customsounds"
    }

    override fun initialize(api: VoicechatApi) {
        this.handler = VoicechatAPIHandler(adapter, api as VoicechatServerApi)
        adapter.getLogger().info("Voicechat Hook initialized!")
    }

    fun playAudio(
        fileName: String,
        player: ServerPlayer?,
        position: AudioPosition,
        volume: Double,
        distance: Double?,
        use3D: Boolean
    ) {
        if (volume < 0.0 || volume > 1.0) {
            throw IllegalArgumentException("Volume must be between 0.0 and 1.0")
        }
        if (distance != null && distance < 0.0) {
            throw IllegalArgumentException("Distance must be greater than 0.0")
        }
        if (!use3D && player == null) {
            throw IllegalArgumentException("Player must be specified for non-3D audio")
        }

        val filePath: Path = adapter.getDataFolder().toPath().resolve(MUSICDATA_FOLDER).resolve(fileName)
        if (!filePath.exists()) {
            adapter.getLogger().warning("Could not find file: $fileName")
            throw IOException("Could not find file: $fileName")
        }

        val ext: String = filePath.fileName.extension
        if (ext != "wav" && ext != "mp3") {
            adapter.getLogger().warning("Unsupported audio extension: $ext")
            throw IOException("Unsupported audio extension: $ext")
        }

        val playerInstance = AudioPlayerInstance(
            handler = handler,
            filePath = filePath,
            position = position,
            volume = volume,
            distance = distance,
            use3D = use3D,
            player = player
        )
        playerInstances[playerInstance.uuid] = playerInstance
        playerInstance.start()
    }

    fun stopAll() {
        adapter.getLogger().info("Stopping all audio players")
        playerInstances.values.forEach { it.stop() }
        playerInstances.clear()
    }

    fun stopForPlayer(player: ServerPlayer) {
        adapter.getLogger().info("Stopping audio players for player: ${player.uuid}")
        playerInstances.entries
            .filter { it.value.player?.uuid == player.uuid }
            .forEach {
                it.value.stop()
                playerInstances.remove(it.key)
            }
    }
}