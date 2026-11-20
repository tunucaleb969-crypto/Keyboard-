package com.kwame.aikeyboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Phase 15: manage text shortcuts (create / edit / delete / enable-disable / search).
 * All rules live in TextShortcutLogic and storage in TextShortcuts; this class is only UI.
 */
class ShortcutsActivity : AppCompatActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var shortcutList: LinearLayout
    private lateinit var editSearch: EditText
    private lateinit var editTrigger: EditText
    private lateinit var editExpansion: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancelEdit: TextView
    private lateinit var txtError: TextView

    /** The ORIGINAL trigger of the shortcut being edited, or null when adding a new one. */
    private var editingTrigger: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcuts)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        scrollView = findViewById(R.id.scrollShortcuts)
        shortcutList = findViewById(R.id.shortcutList)
        editSearch = findViewById(R.id.editSearch)
        editTrigger = findViewById(R.id.editTrigger)
        editExpansion = findViewById(R.id.editExpansion)
        btnSave = findViewById(R.id.btnSaveShortcut)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)
        txtError = findViewById(R.id.txtShortcutError)

        val masterSwitch = findViewById<Switch>(R.id.switchShortcuts)
        masterSwitch.isChecked = TextShortcuts.isEnabled(this)
        masterSwitch.setOnCheckedChangeListener { _, checked ->
            TextShortcuts.setEnabled(this@ShortcutsActivity, checked)
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                renderList()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnSave.setOnClickListener { saveShortcut() }
        btnCancelEdit.setOnClickListener { exitEditMode() }

        renderList()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String?) {
        if (message == null) {
            txtError.visibility = View.GONE
        } else {
            txtError.text = message
            txtError.visibility = View.VISIBLE
        }
    }

    private fun saveShortcut() {
        val trigger = editTrigger.text.toString()
        val expansion = editExpansion.text.toString()
        val original = editingTrigger
        val result = if (original == null) {
            TextShortcuts.add(this, trigger, expansion)
        } else {
            TextShortcuts.update(this, original, trigger, expansion)
        }
        result.onSuccess {
            val wasEditing = original != null
            exitEditMode()
            editSearch.setText("")
            renderList()
            toast(if (wasEditing) "Shortcut updated" else "Shortcut added")
        }.onFailure {
            showError(it.message ?: "Couldn't save this shortcut")
        }
    }

    private fun enterEditMode(shortcut: TextShortcut) {
        editingTrigger = shortcut.trigger
        editTrigger.setText(shortcut.trigger)
        editExpansion.setText(shortcut.expansion)
        btnSave.text = "Save changes"
        btnCancelEdit.visibility = View.VISIBLE
        showError(null)
        scrollView.smoothScrollTo(0, 0)
        editTrigger.requestFocus()
    }

    private fun exitEditMode() {
        editingTrigger = null
        editTrigger.setText("")
        editExpansion.setText("")
        btnSave.text = "Add"
        btnCancelEdit.visibility = View.GONE
        showError(null)
    }

    private fun renderList() {
        shortcutList.removeAllViews()
        val all = TextShortcuts.getAll(this)
        val shown = TextShortcutLogic.search(all, editSearch.text.toString())
        if (shown.isEmpty()) {
            val empty = TextView(this).apply {
                text = if (all.isEmpty()) {
                    "No shortcuts yet. Add one above, for example gm \u2192 Good morning."
                } else {
                    "No shortcuts match your search"
                }
                setTextColor(resources.getColor(R.color.text_secondary, theme))
                textSize = 14f
                setPadding(0, dp(12), 0, dp(12))
            }
            shortcutList.addView(empty)
            return
        }
        shown.forEach { shortcutList.addView(buildRow(it)) }
    }

    private fun buildRow(shortcut: TextShortcut): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.card_background)
            setPadding(dp(14), dp(10), dp(6), dp(10))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(8)
            layoutParams = params
        }

        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { enterEditMode(shortcut) }
        }
        val title = TextView(this).apply {
            text = shortcut.trigger
            setTextColor(resources.getColor(R.color.text_primary, theme))
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val preview = TextView(this).apply {
            text = shortcut.expansion
            setTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 13f
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        textColumn.addView(title)
        textColumn.addView(preview)

        val toggle = Switch(this).apply {
            isChecked = shortcut.enabled
            setOnCheckedChangeListener { _, checked ->
                TextShortcuts.setShortcutEnabled(this@ShortcutsActivity, shortcut.trigger, checked)
            }
        }

        val delete = TextView(this).apply {
            text = "\u2715"
            setTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 18f
            setPadding(dp(14), dp(10), dp(14), dp(10))
            setOnClickListener { confirmDelete(shortcut) }
        }

        row.addView(textColumn)
        row.addView(toggle)
        row.addView(delete)
        return row
    }

    private fun confirmDelete(shortcut: TextShortcut) {
        AlertDialog.Builder(this)
            .setTitle("Delete shortcut?")
            .setMessage("\"${shortcut.trigger}\" will be removed.")
            .setPositiveButton("Delete") { _, _ ->
                TextShortcuts.delete(this, shortcut.trigger)
                if (editingTrigger.equals(shortcut.trigger, ignoreCase = true)) exitEditMode()
                renderList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
