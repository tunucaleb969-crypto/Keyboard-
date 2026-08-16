package com.kwame.aikeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editKey = findViewById<EditText>(R.id.editApiKey)
        editKey.setText(Prefs.getApiKey(this))

        findViewById<Button>(R.id.btnEnableKeyboard).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        findViewById<Button>(R.id.btnChooseKeyboard).setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        findViewById<Button>(R.id.btnSaveKey).setOnClickListener {
            val cleanKey = editKey.text.toString().replace(Regex("[^\\x21-\\x7E]"), "")
            Prefs.setApiKey(this, cleanKey)
            editKey.setText(cleanKey)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.rowTyping).setOnClickListener {
            startActivity(Intent(this, TypingActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowSound).setOnClickListener {
            startActivity(Intent(this, SoundsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowThemes).setOnClickListener {
            startActivity(Intent(this, ThemesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowLayout).setOnClickListener {
            startActivity(Intent(this, LayoutActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowEmoji).setOnClickListener {
            startActivity(Intent(this, EmojisActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowClipboard).setOnClickListener {
            startActivity(Intent(this, ClipboardActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.rowDictionary).setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }
    }
}
