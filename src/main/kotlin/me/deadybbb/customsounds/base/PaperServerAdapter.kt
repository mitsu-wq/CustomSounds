package me.deadybbb.customsounds.base

import org.bukkit.entity.Player
import de.maxhenkel.voicechat.api.Position
import de.maxhenkel.voicechat.api.ServerLevel
import me.deadybbb.customsounds.CustomSounds
import org.bukkit.Bukkit
import org.bukkit.World
import java.io.File
import java.util.logging.Logger

class PaperServerAdapter(
    private var plugin: CustomSounds
): ServerAdapter {

    override fun getDataFolder(): File {
        return plugin.dataFolder
    }

    override fun getLogger(): Logger {
        return plugin.logger
    }

    override fun getOnlinePlayers(): Collection<Player> {
        return Bukkit.getOnlinePlayers()
    }
}