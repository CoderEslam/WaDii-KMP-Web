package com.teacheronline.utils

import kotlinx.browser.window

external interface AudioParam {
    fun setValueAtTime(value: Double, time: Double)
    fun linearRampToValueAtTime(value: Double, time: Double)
}

external class OscillatorNode {
    var type: String
    val frequency: AudioParam
    fun connect(destination: dynamic)
    fun start(`when`: Double = definedExternally)
    fun stop(`when`: Double = definedExternally)
}

external class GainNode {
    val gain: AudioParam
    fun connect(destination: dynamic)
}

external class AudioContext {
    val currentTime: Double
    val destination: dynamic
    fun createOscillator(): OscillatorNode
    fun createGain(): GainNode
    fun close(): dynamic
}

private fun createAudioContext(): AudioContext =
    js("new (window.AudioContext || window.webkitAudioContext)()")

object RingtonePlayer {

    private var audioContext: AudioContext? = null
    private var intervalId: Int? = null

    fun start() {
        if (audioContext != null) return
        val ctx = runCatching { createAudioContext() }.getOrNull() ?: return
        audioContext = ctx
        playRing(ctx)
        intervalId = window.setInterval({ playRing(ctx) }, 2000)
    }

    fun stop() {
        intervalId?.let { window.clearInterval(it) }
        intervalId = null
        audioContext?.let { runCatching { it.close() } }
        audioContext = null
    }

    private fun playRing(ctx: AudioContext) {
        doubleArrayOf(0.0, 0.3).forEach { offset ->
            val start = ctx.currentTime + offset
            val osc = ctx.createOscillator()
            val gain = ctx.createGain()
            osc.type = "sine"
            osc.frequency.setValueAtTime(440.0, start)
            gain.gain.setValueAtTime(0.15, start)
            gain.gain.linearRampToValueAtTime(0.0001, start + 0.25)
            osc.connect(gain)
            gain.connect(ctx.destination)
            osc.start(start)
            osc.stop(start + 0.3)
        }
    }
}
