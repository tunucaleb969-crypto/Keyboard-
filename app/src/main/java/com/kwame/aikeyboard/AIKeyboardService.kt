package com.kwame.aikeyboard

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AIKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var qwertyKeyboard: Keyboard
    private lateinit var symbolsKeyboard: Keyboard
    private var onSymbols = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var pendingSuggestJob: Job? = null
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var capsOn = false
    private var shiftLocked = false
    private var justAutoCapped = false

    private lateinit var wordBtn1: Button
    private lateinit var wordBtn2: Button
    private lateinit var wordBtn3: Button
    private val wordButtons by lazy { listOf(wordBtn1, wordBtn2, wordBtn3) }
    private lateinit var emojiSuggestBtn: Button

    private lateinit var previewPanel: View
    private lateinit var previewText: TextView
    private lateinit var btnPreviewAccept: Button
    private lateinit var btnPreviewCancel: Button

    private lateinit var multiPanel: View
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var btnMultiCancel: Button
    private var multiBefore: String = ""
    private var multiAfter: String = ""
    private var multiReplaces: Boolean = true

    private lateinit var clipboardPanel: View
    private lateinit var clipboardList: LinearLayout
    private lateinit var clipboardManager: ClipboardManager

    private lateinit var emojiPanel: View
    private lateinit var emojiGrid: GridLayout

    private var pendingBefore: String = ""
    private var pendingAfter: String = ""
    private var pendingResult: String = ""

    private val aiClient: AIClient
        get() = AIClient(Prefs.getApiKey(this))

    private val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_container, null)
        keyboardView = root.findViewById(R.id.keyboardView)
        keyboardView.isPreviewEnabled = false

        qwertyKeyboard = Keyboard(this, R.xml.keyboard_qwerty)
        symbolsKeyboard = Keyboard(this, R.xml.keyboard_symbols)
        onSymbols = false
        keyboardView.keyboard = qwertyKeyboard
        keyboardView.setOnKeyboardActionListener(this)

        applyKeyboardHeight()

        wordBtn1 = root.findViewById(R.id.wordSuggest1)
        wordBtn2 = root.findViewById(R.id.wordSuggest2)
        wordBtn3 = root.findViewById(R.id.wordSuggest3)
        wordButtons.forEach { btn ->
            btn.setOnClickListener { insertSuggestedWord(btn.text.toString()) }
        }

        emojiSuggestBtn = root.findViewById(R.id.emojiSuggest)
        emojiSuggestBtn.setOnClickListener {
            currentInputConnection?.commitText(emojiSuggestBtn.text.toString(), 1)
            emojiSuggestBtn.visibility = View.GONE
        }

        root.findViewById<Button>(R.id.btnMic).setOnClickListener { startVoiceInput() }

        clipboardPanel = root.findViewById(R.id.clipboardPanel)
        clipboardList = root.findViewById(R.id.clipboardList)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        root.findViewById<Button>(R.id.btnClipboard).setOnClickListener { toggleClipboardPanel() }

        clipboardManager.addPrimaryClipChangedListener {
            val text = clipboardManager.primaryClip
                ?.takeIf { it.itemCount > 0 }
                ?.getItemAt(0)?.text?.toString()
            if (!text.isNullOrBlank()) {
                Prefs.addClip(this, text)
            }
        }

        emojiPanel = root.findViewById(R.id.emojiPanel)
        emojiGrid = root.findViewById(R.id.emojiGrid)
        root.findViewById<Button>(R.id.btnEmoji).setOnClickListener { toggleEmojiPanel() }

        previewPanel = root.findViewById(R.id.aiPreviewPanel)
        previewText = root.findViewById(R.id.aiPreviewText)
        btnPreviewAccept = root.findViewById(R.id.btnPreviewAccept)
        btnPreviewCancel = root.findViewById(R.id.btnPreviewCancel)

        btnPreviewAccept.setOnClickListener { acceptPreview() }
        btnPreviewCancel.setOnClickListener { hidePreview() }

        multiPanel = root.findViewById(R.id.multiOptionPanel)
        option1 = root.findViewById(R.id.option1)
        option2 = root.findViewById(R.id.option2)
        option3 = root.findViewById(R.id.option3)
        btnMultiCancel = root.findViewById(R.id.btnMultiCancel)
        btnMultiCancel.setOnClickListener { hideMultiPanel() }

        wireToneButton(root, R.id.btnToneProfessional, "professional")
        wireToneButton(root, R.id.btnToneFriendly, "friendly")
        wireToneButton(root, R.id.btnToneCasual, "casual")
        wireToneButton(root, R.id.btnToneFormal, "formal")
        wireToneButton(root, R.id.btnToneFunny, "funny")
        wireToneButton(root, R.id.btnToneFlirty, "flirty")

        root.findViewById<Button>(R.id.btnFixGrammar).setOnClickListener {
            runAiOnFullText("grammar")
        }
        root.findViewById<Button>(R.id.btnSuggestReply).setOnClickListener {
            runAiMulti("reply", replaceText = false)
        }
        root.findViewById<Button>(R.id.btnExplain).setOnClickListener {
            runAiOnFullText("explain", replaceText = false, showInPreviewOnly = true)
        }
        root.findViewById<Button>(R.id.btnCvMode).setOnClickListener {
            runAiOnFullText("cv")
        }
        root.findViewById<Button>(R.id.btnBusinessMode).setOnClickListener {
            runAiOnFullText("business")
        }

        if (Prefs.getAutoCapitalize(this)) {
            capsOn = true
            justAutoCapped = true
            qwertyKeyboard.isShifted = true
        }

        return root
    }

    private fun applyKeyboardHeight() {
        val verticalPadding = when (Prefs.getKeyboardHeight(this)) {
            0 -> 0
            2 -> (18 * resources.displayMetrics.density).toInt()
            else -> (6 * resources.displayMetrics.density).toInt()
        }
        keyboardView.setPadding(0, verticalPadding, 0, verticalPadding)
    }

    private fun toggleEmojiPanel() {
        if (emojiPanel.visibility == View.VISIBLE) {
            emojiPanel.visibility = View.GONE
            return
        }
        if (emojiGrid.childCount == 0) {
            EmojiSuggester.commonEmojis.forEach { emoji ->
                val btn = Button(this, null, 0, R.style.AiChip).apply {
                    text = emoji
                    textSize = 20f
                    setOnClickListener {
                        currentInputConnection?.commitText(emoji, 1)
                    }
                }
                emojiGrid.addView(btn)
            }
        }
        emojiPanel.visibility = View.VISIBLE
    }

    private fun toggleClipboardPanel() {
        if (clipboardPanel.visibility == View.VISIBLE) {
            clipboardPanel.visibility = View.GONE
            return
        }
        val currentClip = clipboardManager.primaryClip
            ?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)?.text?.toString()
        if (!currentClip.isNullOrBlank()) {
            Prefs.addClip(this, currentClip)
        }
        renderClipboardList()
        clipboardPanel.visibility = View.VISIBLE
    }

    private fun renderClipboardList() {
        clipboardList.removeAllViews()
        val clips = Prefs.getClipHistory(this)
        if (clips.isEmpty()) {
            val empty = TextView(this).apply {
                text = "Nothing copied yet"
                setTextColor(0xFFAAAAAA.toInt())
                textSize = 13f
                setPadding(8, 8, 8, 8)
            }
            clipboardList.addView(empty)
            return
        }
        clips.forEach { clip ->
            val chip = Button(this, null, 0, R.style.AiChip).apply {
                text = if (clip.length > 24) clip.take(24) + "…" else clip
                setOnClickListener {
                    currentInputConnection?.commitText(clip, 1)
                    clipboardPanel.visibility = View.GONE
                }
            }
            clipboardList.addView(chip)
        }
    }

    private fun startVoiceInput() {
        VoiceInputActivity.callback = { spokenText ->
            currentInputConnection?.commitText("$spokenText ", 1)
            updateWordSuggestions()
        }
        val intent = Intent(this, VoiceInputActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun wireToneButton(root: View, id: Int, tone: String) {
        root.findViewById<Button>(id).setOnClickListener { runAiMulti(tone) }
    }
}
