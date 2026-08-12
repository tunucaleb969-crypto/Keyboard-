package com.kwame.aikeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast

/**
 * Invisible helper activity. A keyboard (IME) can't directly show Android's
 * voice recognizer popup and get a result back, so this tiny activity does it
 * on the keyboard's behalf, then hands the recognized text back via a callback.
 */
class VoiceInputActivity : Activity() {

    companion object {
        var callback: ((String) -> Unit)? = null
    }

    private val REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
        }
        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "No voice recognizer app found on this device", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = results?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                callback?.invoke(spoken)
            }
        }
        finish()
    }
}
