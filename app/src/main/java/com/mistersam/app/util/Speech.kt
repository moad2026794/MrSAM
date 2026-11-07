package com.mistersam.app.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

/** نطق الإنجليزية (TTS) والتعرّف على صوت المتعلم (STT). */
class Speech(private val ctx: Context) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private var rec: SpeechRecognizer? = null

    val recognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(ctx)

    fun init() {
        tts = TextToSpeech(ctx) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ready = true
            }
        }
    }

    fun speak(text: String, slow: Boolean = false) {
        if (!ready) return
        tts?.setSpeechRate(if (slow) 0.65f else 0.95f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sam")
    }

    fun listen(onPartial: (String) -> Unit, onResult: (String) -> Unit, onError: () -> Unit) {
        if (!recognitionAvailable) {
            onError()
            return
        }
        tts?.stop()
        rec?.destroy()
        rec = SpeechRecognizer.createSpeechRecognizer(ctx).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onError(error: Int) = onError()
                override fun onPartialResults(partialResults: Bundle?) {
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()?.let(onPartial)
                }

                override fun onResults(results: Bundle?) {
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                    if (text.isNullOrBlank()) onError() else onResult(text)
                }
            })
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        rec?.startListening(intent)
    }

    fun release() {
        rec?.destroy()
        rec = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    companion object {
        /** نسبة الكلمات المطلوبة التي نطقها المتعلم (0..100). */
        fun score(target: String, heard: String): Int {
            fun tok(s: String) = s.lowercase()
                .replace(Regex("[^a-z0-9' ]"), " ")
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }

            val t = tok(target)
            val h = tok(heard).toMutableList()
            if (t.isEmpty()) return 0
            var hit = 0
            for (word in t) if (h.remove(word)) hit++
            return hit * 100 / t.size
        }
    }
}
