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
        wordList.addView(buildLearnedSuggestionsRow())
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
                text = "\u2715"
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

    /**
     * Controls for personally-learned next-word suggestions (separate from the fixed
     * dictionary words above): a toggle to turn learning off, and a button to wipe what's
     * already been learned. This is the "view/clear/disable learned data" control for
     * next-word prediction, since that learning is word-pairs with usage counts rather
     * than a per-word list worth showing individually.
     */
    private fun buildLearnedSuggestionsRow(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.card_background)
            setPadding(28, 24, 28, 24)
            val outerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            outerParams.bottomMargin = 16
            layoutParams = outerParams

            val toggleRow = LinearLayout(this@DictionaryActivity).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            val toggleLabel = TextView(this@DictionaryActivity).apply {
                text = "Learn my next-word suggestions"
                setTextColor(resources.getColor(R.color.text_primary, theme))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val toggleSwitch = android.widget.Switch(this@DictionaryActivity).apply {
                isChecked = Prefs.getNextWordLearningEnabled(this@DictionaryActivity)
                setOnCheckedChangeListener { _, checked ->
                    Prefs.setNextWordLearningEnabled(this@DictionaryActivity, checked)
                }
            }
            toggleRow.addView(toggleLabel)
            toggleRow.addView(toggleSwitch)

            val clearRow = TextView(this@DictionaryActivity).apply {
                text = "Clear learned next-word suggestions"
                setTextColor(resources.getColor(R.color.text_secondary, theme))
                textSize = 14f
                setPadding(0, 20, 0, 0)
                setOnClickListener {
                    Prefs.clearLearnedBigrams(this@DictionaryActivity)
                    android.widget.Toast.makeText(this@DictionaryActivity, "Learned suggestions cleared", android.widget.Toast.LENGTH_SHORT).show()
                }
            }

            addView(toggleRow)
            addView(clearRow)
        }
    }
}
