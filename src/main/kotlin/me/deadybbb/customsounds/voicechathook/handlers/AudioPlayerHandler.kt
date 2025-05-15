package me.deadybbb.customsounds.voicechathook.handlers

import de.maxhenkel.voicechat.api.ServerLevel
import de.maxhenkel.voicechat.api.ServerPlayer
import de.maxhenkel.voicechat.api.VoicechatConnection
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel
import me.deadybbb.customsounds.voicechathook.AudioPosition
import org.bukkit.World
import java.util.UUID

class AudioPlayerHandler(val handler: VoicechatAPIHandler) {

    fun getServerLevel(world: World): ServerLevel {
        return handler.api.fromServerLevel(world)
    }

    fun getVoicechatConnection(player: ServerPlayer): VoicechatConnection? {
        return handler.api.getConnectionOf(player)
    }

    fun createStaticAudioChannel(channelID: UUID, player: ServerPlayer): StaticAudioChannel? {
        return handler.api.createStaticAudioChannel(channelID, player.serverLevel, getVoicechatConnection(player))
    }

    fun createStaticAudioPlayer(channelID: UUID, player: ServerPlayer, audioData: ShortArray): AudioPlayer? {
        val staticChannel = createStaticAudioChannel(channelID, player) ?: return null
        return handler.api.createAudioPlayer(staticChannel, handler.api.createEncoder(), audioData)
    }

    fun createLocationalAudioChannel(channelID: UUID, position: AudioPosition): LocationalAudioChannel? {
        return handler.api.createLocationalAudioChannel(
            channelID,
            position.world,
            handler.api.createPosition(position.x, position.y, position.z)
        )
    }

    fun createLocationalAudioPlayer(channelID: UUID, position: AudioPosition, audioData: ShortArray, distance: Double): AudioPlayer? {
        val locationalChannel = createLocationalAudioChannel(channelID, position) ?: return null
        locationalChannel.distance = distance.toFloat()
        return handler.api.createAudioPlayer(locationalChannel, handler.api.createEncoder(), audioData)
    }
}