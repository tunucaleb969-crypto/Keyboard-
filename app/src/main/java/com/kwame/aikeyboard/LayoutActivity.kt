package com.kwame.aikeyboard

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val switchOneHanded = findViewById<Switch>(R.id.switchOneHanded)
        switchOneHanded.isChecked = Prefs.getOneHandedEnabled(this)
        switchOneHanded.setOnCheckedChangeListener { _, checked -> Prefs.setOneHandedEnabled(this, checked) }

        val radioSide = findViewById<RadioGroup>(R.id.radioSide)
        if (Prefs.getOneHandedSide(this) == "left") radioSide.check(R.id.radioLeft) else radioSide.check(R.id.radioRight)
        radioSide.setOnCheckedChangeListener { _, checkedId ->
            Prefs.setOneHandedSide(this, if (checkedId == R.id.radioLeft) "left" else "right")
        }

        val seekWidth = findViewById<SeekBar>(R.id.seekWidth)
        val widthLabel = findViewById<TextView>(R.id.widthLabel)
        val savedWidth = Prefs.getOneHandedWidth(this)
        seekWidth.progress = (savedWidth - 60).coerceIn(0, 40)
        widthLabel.text = "$savedWidth%"
        seekWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val percent = progress + 60
                widthLabel.text = "$percent%"
                if (fromUser) Prefs.setOneHandedWidth(this@LayoutActivity, percent)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val switchSmartBar = findViewById<Switch>(R.id.switchSmartBar)
        switchSmartBar.isChecked = Prefs.getSmartBarEnabled(this)
        switchSmartBar.setOnCheckedChangeListener { _, checked -> Prefs.setSmartBarEnabled(this, checked) }

        val seekDelay = findViewById<SeekBar>(R.id.seekDelay)
        val delayLabel = findViewById<TextView>(R.id.delayLabel)
        val savedDelay = Prefs.getLongPressDelay(this)
        seekDelay.progress = (savedDelay - 100).coerceIn(0, 700)
        delayLabel.text = "$savedDelay ms"
        seekDelay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val ms = progress + 100
                delayLabel.text = "$ms ms"
                if (fromUser) Prefs.setLongPressDelay(this@LayoutActivity, ms)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val switchHintedNumbers = findViewById<Switch>(R.id.switchHintedNumbers)
        switchHintedNumbers.isChecked = Prefs.getHintedNumbersEnabled(this)
        switchHintedNumbers.setOnCheckedChangeListener { _, checked -> Prefs.setHintedNumbersEnabled(this, checked) }
    }
}
