package com.rainbowunicorn.candysalon.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.sin

class SoundManager {

    var bgmEnabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (!value) stopBgm() else if (!isPaused) startBgm()
        }

    var sfxEnabled: Boolean = true

    private val executor = Executors.newSingleThreadExecutor()
    @Volatile
    private var bgmPlaying = false
    @Volatile
    private var isPaused = false
    @Volatile
    private var bgmTrack: AudioTrack? = null

    fun playClick() {
        if (!sfxEnabled) return
        playTone(frequency = 523.25, durationMs = 60, amplitude = 0.3) // C5
    }

    fun playSparkle() {
        if (!sfxEnabled) return
        executor.execute {
            playToneSync(frequency = 659.25, durationMs = 70, amplitude = 0.3) // E5
            playToneSync(frequency = 783.99, durationMs = 70, amplitude = 0.3) // G5
            playToneSync(frequency = 1046.50, durationMs = 120, amplitude = 0.4) // C6
        }
    }

    fun playBubble() {
        if (!sfxEnabled) return
        playTone(frequency = 440.0, durationMs = 80, amplitude = 0.3)
    }

    fun playShutter() {
        if (!sfxEnabled) return
        executor.execute {
            playToneSync(frequency = 1200.0, durationMs = 40, amplitude = 0.4)
            playToneSync(frequency = 600.0, durationMs = 60, amplitude = 0.4)
        }
    }

    fun playVictory() {
        if (!sfxEnabled) return
        executor.execute {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (f in notes) {
                playToneSync(frequency = f, durationMs = 100, amplitude = 0.4)
            }
        }
    }

    fun startBgm() {
        if (!bgmEnabled || bgmPlaying) return
        bgmPlaying = true
        executor.execute {
            val sampleRate = 22050
            val numSamples = sampleRate * 4 // 4 second loop
            val buffer = ShortArray(numSamples)
            val melody = doubleArrayOf(261.63, 329.63, 392.00, 523.25) // C4, E4, G4, C5

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val noteIdx = ((t * 2).toInt()) % melody.size
                val freq = melody[noteIdx]
                val sample = (sin(2.0 * Math.PI * freq * t) * 0.15 * Short.MAX_VALUE).toInt().toShort()
                buffer[i] = sample
            }

            try {
                bgmTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                bgmTrack?.write(buffer, 0, buffer.size)
                bgmTrack?.setLoopPoints(0, buffer.size, -1) // Infinite loop
                bgmTrack?.play()
            } catch (_: Exception) {
                // Audio device fallback
            }
        }
    }

    @Synchronized
    fun stopBgm() {
        bgmPlaying = false
        try {
            bgmTrack?.stop()
            bgmTrack?.release()
            bgmTrack = null
        } catch (_: Exception) {}
    }

    fun onPause() {
        isPaused = true
        stopBgm()
    }

    fun onResume() {
        isPaused = false
        if (bgmEnabled) {
            startBgm()
        }
    }

    fun release() {
        stopBgm()
        executor.shutdownNow()
    }

    private fun playTone(frequency: Double, durationMs: Int, amplitude: Double) {
        executor.execute {
            playToneSync(frequency, durationMs, amplitude)
        }
    }

    private fun playToneSync(frequency: Double, durationMs: Int, amplitude: Double) {
        val sampleRate = 22050
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(count)

        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val sample = (sin(2.0 * Math.PI * frequency * t) * amplitude * Short.MAX_VALUE).toInt().toShort()
            samples[i] = sample
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.release()
        } catch (_: Exception) {}
    }
}
