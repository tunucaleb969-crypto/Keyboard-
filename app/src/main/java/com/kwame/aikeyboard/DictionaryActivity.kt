package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DictionaryActivity : AppCompatActivity() {

    private lateinit var wordList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        wordList = findViewById(R.id.wordList)

        val editNewWord = findViewById<EditText>(R.id.editNewWord)
        findViewById<Button>(R.id.btnAddWord).setOnClickListener {
            val word = editNewWord.text.toString().trim()
            if (word.isNotBlank()) {
                Prefs.addDictionaryWord(this, word)
                editNewWord.setText("")
                renderList()
            }
        }

        renderList()
    }

    private fun renderList() {
        wordList.removeAllViews()
        val words = Prefs.getDictionaryWords(this)
        if (words.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No words added yet"
                setTextColor(resources.getColor(R.color.text_secondary, theme))
                textSize = 14f
                setPadding(0, 20, 0, 20)
            }
            wordList.addView(empty)
            return
        }
        words.forEach { word ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundResource(R.drawable.card_background)
                setPadding(28, 24, 28, 24)
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.bottomMargin = 16
                layoutParams = params
            }
            val label = TextView(this).apply {
                text = word
                setTextColor(resources.getColor(R.color.text_primary, theme))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val delete = TextView(this).apply {
                text = "✕"
                setTextColor(resources.getColor(R.color.text_secondary, theme))
                textSize = 16f
                setPadding(20, 0, 0, 0)
                setOnClickListener {
                    Prefs.removeDictionaryWord(this@DictionaryActivity, word)
                    renderList()
                }
            }
            row.addView(label)
            row.addView(delete)
            wordList.addView(row)
        }
    }
}
