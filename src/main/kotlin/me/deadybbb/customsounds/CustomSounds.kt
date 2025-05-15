package me.deadybbb.customsounds

import de.maxhenkel.voicechat.api.BukkitVoicechatService
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import me.deadybbb.customsounds.base.LegacyTextHandler
import me.deadybbb.customsounds.base.PaperServerAdapter
import me.deadybbb.customsounds.base.ServerAdapter
import me.deadybbb.customsounds.voicechathook.VoicechatHook
import org.bukkit.plugin.java.JavaPlugin

class CustomSounds : JavaPlugin() {
    private val adapter: ServerAdapter = PaperServerAdapter(this)
    val formatter: LegacyTextHandler = LegacyTextHandler()
    lateinit var hook: VoicechatHook
    private lateinit var command: CustomSoundsCommand

    override fun onLoad() {

    }

    override fun onEnable() {
        hook = VoicechatHook(adapter)
        server.servicesManager.load(BukkitVoicechatService::class.java)?.registerPlugin(hook)
        command = CustomSoundsCommand(this)
        command.registerCommands()
    }

    override fun onDisable() {
        hook.stopAll()
    }
}