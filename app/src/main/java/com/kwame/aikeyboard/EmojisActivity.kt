package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EmojisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emojis)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val switchEmojiSuggest = findViewById<Switch>(R.id.switchEmojiSuggest)
        switchEmojiSuggest.isChecked = Prefs.getWordSuggestionsEnabled(this)
        switchEmojiSuggest.setOnCheckedChangeListener { _, checked -> Prefs.setWordSuggestionsEnabled(this, checked) }
    }
}
