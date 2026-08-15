package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TypingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_typing)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val switchWordSuggest = findViewById<Switch>(R.id.switchWordSuggest)
        val switchAutoCorrect = findViewById<Switch>(R.id.switchAutoCorrect)
        val switchAutoCap = findViewById<Switch>(R.id.switchAutoCap)

        switchWordSuggest.isChecked = Prefs.getWordSuggestionsEnabled(this)
        switchAutoCorrect.isChecked = Prefs.getAutoCorrectEnabled(this)
        switchAutoCap.isChecked = Prefs.getAutoCapitalize(this)

        switchWordSuggest.setOnCheckedChangeListener { _, checked -> Prefs.setWordSuggestionsEnabled(this, checked) }
        switchAutoCorrect.setOnCheckedChangeListener { _, checked -> Prefs.setAutoCorrectEnabled(this, checked) }
        switchAutoCap.setOnCheckedChangeListener { _, checked -> Prefs.setAutoCapitalize(this, checked) }
    }
}
