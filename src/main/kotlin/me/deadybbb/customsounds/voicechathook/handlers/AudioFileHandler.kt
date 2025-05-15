package me.deadybbb.customsounds.voicechathook.handlers

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.io.path.extension

class AudioFileHandler(
    private val handler: VoicechatAPIHandler
) {
    companion object {
        private val FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000f, 16, 1, 2, 48000f, false)
    }

    fun readSoundFile(file: Path, volume: Double): ShortArray {
        handler.adapter.getLogger().info("Reading file: $file")
        val bytes = convertFormat(file)
        return handler.api.audioConverter.bytesToShorts(adjustVolume(bytes, volume))
    }

    private fun convertFormat(file: Path): ByteArray {
        var pcmData: ByteArray
        val ext = file.extension

        when (ext) {
            "wav" -> {
                handler.adapter.getLogger().info("Wav file: $file")
                val finalInputStream = AudioSystem.getAudioInputStream(FORMAT, AudioSystem.getAudioInputStream(file.toFile()))
                if (finalInputStream == null) {
                    handler.adapter.getLogger().warning("Couldn't convert file: $file")
                    throw IOException("Couldn't convert file: $file")
                }
                pcmData = finalInputStream.readAllBytes()
                finalInputStream.close()
            }
            "mp3" -> {
                handler.adapter.getLogger().info("MP3 file: $file")
                val mp3Decoder = handler.api.createMp3Decoder(Files.newInputStream(file))
                if (mp3Decoder == null) {
                    handler.adapter.getLogger().warning("Couldn't convert file: $file")
                    throw IOException("Couldn't convert file: $file")
                }
                pcmData = handler.api.audioConverter.shortsToBytes(mp3Decoder.decode())
            }
            else -> {
                handler.adapter.getLogger().warning("Unknown file extension: $ext")
                throw IOException("Unknown file extension: $ext")
            }
        }
        return pcmData
    }

    private fun adjustVolume(audio: ByteArray, volume: Double): ByteArray {
        if (volume < 0.0 || volume > 1.0) {
            handler.adapter.getLogger().warning("Volume must be between 0.0 and 1.0")
            return audio
        }
        val array = ByteArray(audio.size)
        for (i in audio.indices step 2) {
            val buf1 = audio[i + 1].toInt()
            val buf2 = audio[i].toInt()
            val res = ((buf1 and 0xff shl 8) or (buf2 and 0xff)) * volume
            array[i] = res.toInt().toByte()
            array[i + 1] = (res.toInt() shr 8).toByte()
        }
        return array
    }
}