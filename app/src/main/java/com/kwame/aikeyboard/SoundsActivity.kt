package com.kwame.aikeyboard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.SeekBar
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
        val switchLongPressSound = findViewById<Switch>(R.id.switchLongPressSound)
        val switchRepeatedVibrate = findViewById<Switch>(R.id.switchRepeatedVibrate)

        switchSound.isChecked = Prefs.getSoundEnabled(this)
        switchVibrate.isChecked = Prefs.getVibrateEnabled(this)
        switchLongPressSound.isChecked = Prefs.getLongPressSoundEnabled(this)
        switchRepeatedVibrate.isChecked = Prefs.getRepeatedVibrateEnabled(this)

        switchSound.setOnCheckedChangeListener { _, checked -> Prefs.setSoundEnabled(this, checked) }
        switchVibrate.setOnCheckedChangeListener { _, checked -> Prefs.setVibrateEnabled(this, checked) }
        switchLongPressSound.setOnCheckedChangeListener { _, checked -> Prefs.setLongPressSoundEnabled(this, checked) }
        switchRepeatedVibrate.setOnCheckedChangeListener { _, checked -> Prefs.setRepeatedVibrateEnabled(this, checked) }

        val seekVolume = findViewById<SeekBar>(R.id.seekVolume)
        seekVolume.progress = Prefs.getSoundVolume(this)
        seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) Prefs.setSoundVolume(this@SoundsActivity, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val seekVibrateDuration = findViewById<SeekBar>(R.id.seekVibrateDuration)
        val vibrateDurationLabel = findViewById<TextView>(R.id.vibrateDurationLabel)
        seekVibrateDuration.progress = Prefs.getVibrateDuration(this)
        vibrateDurationLabel.text = "${Prefs.getVibrateDuration(this)} ms"
        seekVibrateDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                vibrateDurationLabel.text = "$progress ms"
                if (fromUser) Prefs.setVibrateDuration(this@SoundsActivity, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<Button>(R.id.btnTestVibrate).setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            try {
                val duration = Prefs.getVibrateDuration(this)
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration.toLong())
                }
                Toast.makeText(this, "Vibration triggered — did you feel it?", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Vibration error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
