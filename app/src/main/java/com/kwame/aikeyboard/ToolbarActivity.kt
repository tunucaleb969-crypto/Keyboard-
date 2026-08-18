package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ToolbarActivity : AppCompatActivity() {

    // Maps a stable chip ID to its display label — must match IDs used in AIKeyboardService.
    private val chips = listOf(
        "fix" to "✓ Fix",
        "professional" to "Professional",
        "friendly" to "Friendly",
        "casual" to "Casual",
        "formal" to "Formal",
        "funny" to "Funny",
        "flirty" to "Flirty",
        "polite" to "Polite",
        "confident" to "Confident",
        "reply" to "💬 Reply",
        "decline" to "🚫 Decline",
        "explain" to "❓ Explain",
        "cv" to "📄 CV",
        "business" to "💼 Business",
        "shorten" to "✂️ Shorten",
        "expand" to "↔️ Expand",
        "translate" to "🌐 Translate",
        "selectall" to "Select All",
        "copy" to "Copy",
        "cut" to "Cut",
        "paste" to "Paste",
        "search" to "🔍 Search"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val list = findViewById<LinearLayout>(R.id.chipToggleList)
        val hiddenChips = Prefs.getVisibleToolbarChips(this) // stores HIDDEN chip ids

        chips.forEach { (id, label) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundResource(R.drawable.card_background)
                setPadding(28, 24, 28, 24)
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.bottomMargin = 16
                layoutParams = params
            }
            val text = TextView(this).apply {
                text = label
                setTextColor(resources.getColor(R.color.text_primary, theme))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val toggle = Switch(this).apply {
                isChecked = id !in hiddenChips
                setOnCheckedChangeListener { _, checked ->
                    val current = Prefs.getVisibleToolbarChips(this@ToolbarActivity).toMutableSet()
                    if (checked) current.remove(id) else current.add(id)
                    Prefs.setVisibleToolbarChips(this@ToolbarActivity, current)
                }
            }
            row.addView(text)
            row.addView(toggle)
            list.addView(row)
        }
    }
}
