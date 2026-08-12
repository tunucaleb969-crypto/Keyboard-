package com.kwame.aikeyboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
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
            val cleanKey = editKey.text.toString().replace(Regex("\\s"), "")
            Prefs.setApiKey(this, cleanKey)
            editKey.setText(cleanKey)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        val switchSound = findViewById<Switch>(R.id.switchSound)
        val switchVibrate = findViewById<Switch>(R.id.switchVibrate)
        val switchAutoCap = findViewById<Switch>(R.id.switchAutoCap)
        val radioHeight = findViewById<RadioGroup>(R.id.radioHeight)

        switchSound.isChecked = Prefs.getSoundEnabled(this)
        switchVibrate.isChecked = Prefs.getVibrateEnabled(this)
        switchAutoCap.isChecked = Prefs.getAutoCapitalize(this)

        when (Prefs.getKeyboardHeight(this)) {
            0 -> radioHeight.check(R.id.radioSmall)
            2 -> radioHeight.check(R.id.radioLarge)
            else -> radioHeight.check(R.id.radioMedium)
        }

        switchSound.setOnCheckedChangeListener { _, checked -> Prefs.setSoundEnabled(this, checked) }
        switchVibrate.setOnCheckedChangeListener { _, checked -> Prefs.setVibrateEnabled(this, checked) }
        switchAutoCap.setOnCheckedChangeListener { _, checked -> Prefs.setAutoCapitalize(this, checked) }

        radioHeight.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                R.id.radioSmall -> 0
                R.id.radioLarge -> 2
                else -> 1
            }
            Prefs.setKeyboardHeight(this, value)
        }

        findViewById<Button>(R.id.btnTestVibrate).setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
                Toast.makeText(this, "Vibration triggered — did you feel it?", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Vibration error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
