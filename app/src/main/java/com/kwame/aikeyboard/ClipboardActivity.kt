package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClipboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipboard)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val switchClipboardSuggest = findViewById<Switch>(R.id.switchClipboardSuggest)
        switchClipboardSuggest.isChecked = Prefs.getClipboardSuggestionsEnabled(this)
        switchClipboardSuggest.setOnCheckedChangeListener { _, checked -> Prefs.setClipboardSuggestionsEnabled(this, checked) }

        findViewById<Button>(R.id.btnClearClips).setOnClickListener {
            Prefs.clearClips(this)
            Toast.makeText(this, "Clipboard history cleared", Toast.LENGTH_SHORT).show()
        }
    }
}
