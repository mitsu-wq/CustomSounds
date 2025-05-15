package me.deadybbb.customsounds.voicechathook.handlers

import de.maxhenkel.voicechat.api.VoicechatServerApi
import me.deadybbb.customsounds.base.ServerAdapter

class VoicechatAPIHandler(
    val adapter: ServerAdapter,
    val api: VoicechatServerApi,
) {
    val audioFileHandler = AudioFileHandler(this)
    val audioPlayerHandler = AudioPlayerHandler(this)
}