package me.deadybbb.customsounds.voicechathook

import de.maxhenkel.voicechat.api.ServerPlayer
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer
import me.deadybbb.customsounds.voicechathook.handlers.VoicechatAPIHandler
import java.nio.file.Path
import java.util.Collections
import java.util.UUID

class AudioPlayerInstance(
    private val handler: VoicechatAPIHandler,
    private val filePath: Path,
    val player: ServerPlayer?,
    private val position: AudioPosition,
    private val volume: Double,
    private val distance: Double?,
    private val use3D: Boolean
) {
    val uuid: UUID = UUID.randomUUID()
    private val audioThread: Thread = Thread(this::play, "AudioPlayerThread-$uuid")
    private val audioPlayers: MutableList<AudioPlayer> = Collections.synchronizedList(mutableListOf<AudioPlayer>())

    fun start() {
        handler.adapter.getLogger().info("Starting AudioPlayer: $uuid (3D: $use3D, Player: ${player?.uuid ?: "none"})")
        audioThread.start()
    }

    fun stop() {
        handler.adapter.getLogger().info("Stopping AudioPlayer: $uuid")
        synchronized(audioPlayers) {
            audioPlayers.forEach { it.stopPlaying() }
            audioPlayers.clear()
        }
        audioThread.interrupt()
    }

    private fun play() {
        try {
            val audioData = handler.audioFileHandler.readSoundFile(filePath, volume)
            val audioPlayer = if (use3D) {
                if (distance == null) {
                    handler.adapter.getLogger().warning("Distance is required to 3D audio: $filePath")
                    return
                }
                handler.audioPlayerHandler.createLocationalAudioPlayer(uuid, position, audioData, distance)
            } else {
                if (player == null) {
                    handler.adapter.getLogger().warning("Player is required for non-3D audio: $filePath")
                    return
                }
                handler.audioPlayerHandler.createStaticAudioPlayer(uuid, player, audioData)
            }

            if (audioPlayer == null) {
                handler.adapter.getLogger().warning("Couldn't create AudioPlayer for $filePath (Player: ${player?.uuid ?: "none"})")
                return
            }

            synchronized(audioPlayers) {
                audioPlayers.add(audioPlayer)
            }

            if (audioThread.isInterrupted) {
                handler.adapter.getLogger().warning("Audio thread was interrupted before playing: $uuid")
                return
            }

            handler.adapter.getLogger().info("Playing audio: $filePath (3D: $use3D, Player: ${player?.uuid ?: "none"})")
            audioPlayer.startPlaying()
            audioPlayer.setOnStopped {
                handler.adapter.getLogger().info("AudioPlayer was stopped: $uuid")
                synchronized(audioPlayers) {
                    audioPlayers.remove(audioPlayer)
                    if (audioPlayers.isEmpty()) {
                        audioThread.interrupt()
                    }
                }
            }
        } catch (ex: Exception) {
            handler.adapter.getLogger().warning("Failed to play audio: ${ex.message}")
            stop()
        }
    }
}