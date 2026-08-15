package com.kwame.aikeyboard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SoundsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sounds)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val switchSound = findViewById<Switch>(R.id.switchSound)
        val switchVibrate = findViewById<Switch>(R.id.switchVibrate)

        switchSound.isChecked = Prefs.getSoundEnabled(this)
        switchVibrate.isChecked = Prefs.getVibrateEnabled(this)

        switchSound.setOnCheckedChangeListener { _, checked -> Prefs.setSoundEnabled(this, checked) }
        switchVibrate.setOnCheckedChangeListener { _, checked -> Prefs.setVibrateEnabled(this, checked) }

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
