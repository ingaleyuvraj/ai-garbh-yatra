package com.garbhyatra.app.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Lightweight wrapper around Android [TextToSpeech] tuned for a soft, gentle Marathi
 * (Hindi fallback) female voice. Reads गर्भसंवाद / affirmation text aloud. State is
 * Compose-observable so buttons can toggle between play and stop.
 */
class GarbhTts(context: Context) {
    private var tts: TextToSpeech? = null

    /** True once the engine is initialised and a usable language is set. */
    var ready by mutableStateOf(false)
        private set

    /** Key of the item currently being spoken, or null when idle. */
    var speakingKey by mutableStateOf<String?>(null)
        private set

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                configure()
                ready = true
            }
        }
    }

    private fun configure() {
        val engine = tts ?: return
        // Prefer Marathi; fall back to Hindi if Marathi data is unavailable.
        val marathi = Locale("mr", "IN")
        val res = engine.setLanguage(marathi)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(Locale("hi", "IN"))
        }
        selectFemaleVoice(engine)
        // Soft, calm delivery: gentle pitch, slightly slower than normal.
        engine.setPitch(1.05f)
        engine.setSpeechRate(0.9f)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { speakingKey = null }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { speakingKey = null }
            override fun onError(utteranceId: String?, errorCode: Int) { speakingKey = null }
        })
    }

    private fun selectFemaleVoice(engine: TextToSpeech) {
        val voices = try { engine.voices } catch (_: Exception) { null } ?: return
        val indian = voices.filter { it.locale?.language == "mr" || it.locale?.language == "hi" }
        // Prefer a voice explicitly tagged female; otherwise avoid explicitly male ones.
        val female = indian.firstOrNull { it.name.lowercase().contains("female") }
            ?: indian.firstOrNull {
                val n = it.name.lowercase()
                n.contains("female") || !n.contains("male")
            }
        female?.let { runCatching { engine.voice = it } }
    }

    /** Speaks [text] for [key]; tapping the same [key] again stops playback. */
    fun toggle(key: String, text: String) {
        val engine = tts ?: return
        if (text.isBlank()) return
        if (speakingKey == key) {
            stop()
            return
        }
        speakingKey = key
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, key)
    }

    fun stop() {
        tts?.stop()
        speakingKey = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

/** Remembers a [GarbhTts] tied to the composition lifecycle and shuts it down on dispose. */
@Composable
fun rememberGarbhTts(): GarbhTts {
    val context = LocalContext.current
    val tts = remember { GarbhTts(context) }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }
    return tts
}
