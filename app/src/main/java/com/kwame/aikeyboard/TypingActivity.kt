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

        bindSwitch(R.id.switchWordSuggest, Prefs::getWordSuggestionsEnabled, Prefs::setWordSuggestionsEnabled)
        bindSwitch(R.id.switchAutoCorrect, Prefs::getAutoCorrectEnabled, Prefs::setAutoCorrectEnabled)
        bindSwitch(R.id.switchAutoCap, Prefs::getAutoCapitalize, Prefs::setAutoCapitalize)
        bindSwitch(R.id.switchUndoAutocorrect, Prefs::getUndoAutocorrectEnabled, Prefs::setUndoAutocorrectEnabled)
        bindSwitch(R.id.switchDoubleSpace, Prefs::getDoubleSpacePeriodEnabled, Prefs::setDoubleSpacePeriodEnabled)
        bindSwitch(R.id.switchAutoSpacePunct, Prefs::getAutoSpacePunctuationEnabled, Prefs::setAutoSpacePunctuationEnabled)
        bindSwitch(R.id.switchRememberCaps, Prefs::getRememberCapsEnabled, Prefs::setRememberCapsEnabled)
        bindSwitch(R.id.switchClipboardSuggest, Prefs::getClipboardSuggestionsEnabled, Prefs::setClipboardSuggestionsEnabled)
    }

    private fun bindSwitch(
        id: Int,
        getter: (android.content.Context) -> Boolean,
        setter: (android.content.Context, Boolean) -> Unit
    ) {
        val switch = findViewById<Switch>(id)
        switch.isChecked = getter(this)
        switch.setOnCheckedChangeListener { _, checked -> setter(this, checked) }
    }
}
