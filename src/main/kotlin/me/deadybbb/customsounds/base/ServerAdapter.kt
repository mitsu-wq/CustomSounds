package me.deadybbb.customsounds.base

import org.bukkit.entity.Player
import java.io.File
import java.util.logging.Logger

public interface ServerAdapter {
    fun getDataFolder(): File
    fun getLogger(): Logger
    fun getOnlinePlayers(): Collection<Player>
}