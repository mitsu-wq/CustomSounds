package me.deadybbb.customsounds.base

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import java.util.regex.Pattern

class LegacyTextHandler {
    companion object {
        private val LEGACY_PATTERN: Pattern = Pattern.compile("&[0-9a-fk-or]")
    }

    fun parseText(text: String): Component {
        var msg: Component
        if (LEGACY_PATTERN.matcher(text).find()) {
            msg = LegacyComponentSerializer.legacyAmpersand().deserialize(text)
        } else {
            msg = MiniMessage.miniMessage().deserialize(text)
        }
        return msg
    }

    fun sendFormattedMessage(sender: CommandSender, text: String) {
        sender.sendMessage(parseText(text))
    }
}