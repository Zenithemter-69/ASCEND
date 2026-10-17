package com.watchparty.app.sync

import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlin.math.abs

class SyncEngine(
    private val player: ExoPlayer,
    private val onPositionUpdate: (Long) -> Unit
) {
    private var syncJob: Job? = null
    private var serverTimeOffset: Long = 0
    private var scheduledPlayTime: Long? = null
    private var targetPosition: Long = 0
    private var lastDriftCorrectionTime: Long = 0

    // Drift thresholds in milliseconds
    private val DRIFT_IGNORE_THRESHOLD = 40L
    private val DRIFT_GENTLE_THRESHOLD = 100L
    private val DRIFT_STRONG_THRESHOLD = 250L
    private val DRIFT_RESYNC_THRESHOLD = 500L

    // Playback speed adjustment range
    private val SPEED_NORMAL = 1.0f
    private val SPEED_GENTLE_SLOW = 0.98f
    private val SPEED_GENTLE_FAST = 1.02f
    private val SPEED_STRONG_SLOW = 0.96f
    private val SPEED_STRONG_FAST = 1.04f

    fun updateServerTimeOffset(clientSendTime: Long, serverTime: Long, clientReceiveTime: Long) {
        val roundTripTime = clientReceiveTime - clientSendTime
        val estimatedServerTime = serverTime + (roundTripTime / 2)
        serverTimeOffset = estimatedServerTime - clientReceiveTime
    }

    fun schedulePlay(playAtServerTime: Long, position: Long) {
        scheduledPlayTime = playAtServerTime
        targetPosition = position

        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.Main).launch {
            val currentServerTime = System.currentTimeMillis() + serverTimeOffset
            val delayMs = playAtServerTime - currentServerTime

            if (delayMs > 0) {
                // Prepare player at target position before playing
                player.seekTo(position)
                player.pause()
                delay(delayMs)
            }

            player.play()
            startDriftMonitoring()
        }
    }

    fun pause() {
        syncJob?.cancel()
        player.pause()
        resetSpeed()
    }

    fun seek(position: Long) {
        targetPosition = position
        player.seekTo(position)
        // Reset speed after seeking to avoid immediate correction overshoot
        resetSpeed()
    }

    fun stop() {
        syncJob?.cancel()
        player.stop()
        resetSpeed()
    }

    private fun resetSpeed() {
        if (player.playbackParameters.speed != SPEED_NORMAL) {
            player.setPlaybackSpeed(SPEED_NORMAL)
        }
    }

    private fun startDriftMonitoring() {
        syncJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(200) // Check drift every 200ms

                if (!player.isPlaying) continue

                val currentPosition = player.currentPosition
                val currentServerTime = System.currentTimeMillis() + serverTimeOffset
                val expectedPosition = targetPosition + (currentServerTime - (scheduledPlayTime ?: currentServerTime))
                val drift = expectedPosition - currentPosition

                // Proportional-like correction logic
                applyCorrection(drift, expectedPosition)

                // Periodic position update to server
                onPositionUpdate(currentPosition)
            }
        }
    }

    private fun applyCorrection(drift: Long, expectedPosition: Long) {
        val absDrift = abs(drift)
        val now = System.currentTimeMillis()

        when {
            absDrift < DRIFT_IGNORE_THRESHOLD -> {
                resetSpeed()
            }
            absDrift < DRIFT_GENTLE_THRESHOLD -> {
                val speed = if (drift > 0) SPEED_GENTLE_FAST else SPEED_GENTLE_SLOW
                player.setPlaybackSpeed(speed)
            }
            absDrift < DRIFT_STRONG_THRESHOLD -> {
                val speed = if (drift > 0) SPEED_STRONG_FAST else SPEED_STRONG_SLOW
                player.setPlaybackSpeed(speed)
            }
            absDrift < DRIFT_RESYNC_THRESHOLD -> {
                // If drift persists despite speed changes, perform a small seek
                if (now - lastDriftCorrectionTime > 1000) {
                    player.seekTo(expectedPosition)
                    lastDriftCorrectionTime = now
                }
            }
            else -> {
                // Large drift: Hard resync
                player.seekTo(expectedPosition)
                resetSpeed()
                lastDriftCorrectionTime = now
            }
        }
    }
}
