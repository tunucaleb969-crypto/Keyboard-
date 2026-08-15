package com.kwame.aikeyboard

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThemesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val grid = findViewById<GridLayout>(R.id.themeGrid)
        val currentThemeId = Prefs.getTheme(this)
        val density = resources.displayMetrics.density
        val swatchSize = (88 * density).toInt()
        val margin = (6 * density).toInt()

        KeyboardThemes.all.forEach { theme ->
            val swatch = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = swatchSize
                    height = swatchSize
                    setMargins(margin, margin, margin, margin)
                }
                background = GradientDrawable().apply {
                    setColor(theme.background)
                    cornerRadius = 16 * density
                    if (theme.id == currentThemeId) {
                        setStroke((3 * density).toInt(), theme.accentColor)
                    }
                }
                setOnClickListener {
                    Prefs.setTheme(this@ThemesActivity, theme.id)
                    recreate()
                }
            }

            val label = TextView(this).apply {
                text = theme.displayName
                setTextColor(theme.textColor)
                textSize = 12f
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply { gravity = Gravity.CENTER }
            }
            swatch.addView(label)
            grid.addView(swatch)
        }
    }
}
