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
            runAiOnFullText("reply", replaceText = false)
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
        root.findViewById<Button>(id).setOnClickListener { runAiOnFullText(tone) }
    }

    private fun runAiOnFullText(task: String, replaceText: Boolean = true) {
        val ic = currentInputConnection ?: return
        if (Prefs.getApiKey(this).isBlank()) {
            toast("Add your API key in the AI Keyboard app first")
            return
        }
        val before = ic.getTextBeforeCursor(4000, 0)?.toString().orEmpty()
        val after = ic.getTextAfterCursor(4000, 0)?.toString().orEmpty()
        val fullText = before + after
        if (fullText.isBlank()) {
            toast("Nothing to work with yet")
            return
        }

        toast("Thinking…")
        scope.launch {
            aiClient.run(task, fullText).onSuccess { result ->
                if (replaceText) {
                    pendingBefore = before
                    pendingAfter = after
                    pendingResult = result
                    showPreview(result)
                } else {
                    showReplyOptions(result)
                }
            }.onFailure {
                toast("AI request failed: ${it.message}")
            }
        }
    }

    private fun showPreview(result: String) {
        previewText.text = result
        previewPanel.visibility = View.VISIBLE
    }

    private fun hidePreview() {
        previewPanel.visibility = View.GONE
        pendingBefore = ""
        pendingAfter = ""
        pendingResult = ""
    }

    private fun acceptPreview() {
        val ic = currentInputConnection ?: return
        if (pendingResult.isBlank()) return
        ic.beginBatchEdit()
        ic.deleteSurroundingText(pendingBefore.length, pendingAfter.length)
        ic.commitText(pendingResult, 1)
        ic.endBatchEdit()
        hidePreview()
    }

    private fun showReplyOptions(raw: String) {
        val first = raw.lines().firstOrNull { it.isNotBlank() } ?: return
        Toast.makeText(this, "Tap again to insert: $first", Toast.LENGTH_LONG).show()
        currentInputConnection?.commitText(first, 1)
    }

    private fun updateWordSuggestions() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(40, 0)?.toString().orEmpty()
        val currentWord = before.substringAfterLast(" ").substringAfterLast("\n")

        val matches = WordSuggester.suggest(currentWord)
        wordButtons.forEachIndexed { index, btn ->
            val word = matches.getOrNull(index)
            if (word != null) {
                btn.text = word
                btn.visibility = View.VISIBLE
            } else {
                btn.visibility = View.GONE
            }
        }

        val lastWord = before.trim().substringAfterLast(" ")
        val emoji = if (lastWord.isNotBlank()) EmojiSuggester.suggestForWord(lastWord) else null
        if (emoji != null && before.endsWith(" ")) {
            emojiSuggestBtn.text = emoji
            emojiSuggestBtn.visibility = View.VISIBLE
        } else {
            emojiSuggestBtn.visibility = View.GONE
        }
    }

    private fun insertSuggestedWord(word: String) {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(40, 0)?.toString().orEmpty()
        val currentWord = before.substringAfterLast(" ").substringAfterLast("\n")
        ic.deleteSurroundingText(currentWord.length, 0)
        ic.commitText("$word ", 1)
        wordButtons.forEach { it.visibility = View.GONE }
    }

    private fun scheduleLiveCheck() {
        pendingSuggestJob?.cancel()
        debounceHandler.removeCallbacksAndMessages(null)
        debounceHandler.postDelayed({
            val ic = currentInputConnection ?: return@postDelayed
            val before = ic.getTextBeforeCursor(200, 0)?.toString().orEmpty()
            if (before.trim().split(" ").size >= 3 && Prefs.getApiKey(this).isNotBlank()) {
                // Real build: call aiClient.run("grammar", before) here.
            }
        }, 900)
    }

    private fun autoUnshift() {
        if (capsOn && !shiftLocked) {
            capsOn = false
            qwertyKeyboard.isShifted = false
            keyboardView.invalidateAllKeys()
        }
    }

    private fun toggleSymbols() {
        onSymbols = !onSymbols
        keyboardView.keyboard = if (onSymbols) symbolsKeyboard else qwertyKeyboard
        keyboardView.invalidateAllKeys()
    }

    private fun playKeyFeedback() {
        if (Prefs.getSoundEnabled(this)) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
        if (Prefs.getVibrateEnabled(this)) {
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(12)
            }
        }
    }

    private fun maybeAutoCapitalize(ic: InputConnection) {
        if (!Prefs.getAutoCapitalize(this) || onSymbols) return
        val before = ic.getTextBeforeCursor(3, 0)?.toString().orEmpty()
        val endsSentence = before.endsWith(". ") || before.endsWith("! ") || before.endsWith("? ") || before.trim().isEmpty()
        if (endsSentence && !capsOn) {
            capsOn = true
            justAutoCapped = true
            qwertyKeyboard.isShifted = true
            keyboardView.invalidateAllKeys()
        }
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        try {
            onKeyInner(primaryCode, keyCodes)
        } catch (e: Exception) {
            Toast.makeText(this, "CRASH: ${e.javaClass.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun onKeyInner(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection = currentInputConnection ?: return
        playKeyFeedback()
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
                updateWordSuggestions()
            }
            -1 -> {
                if (onSymbols) return
                if (capsOn) {
                    shiftLocked = !shiftLocked
                } else {
                    capsOn = true
                    shiftLocked = false
                }
                justAutoCapped = false
                qwertyKeyboard.isShifted = capsOn
                keyboardView.invalidateAllKeys()
            }
            -2 -> toggleSymbols()
            10 -> {
                ic.commitText("\n", 1)
                wordButtons.forEach { it.visibility = View.GONE }
                maybeAutoCapitalize(ic)
            }
            32 -> {
                ic.commitText(" ", 1)
                wordButtons.forEach { it.visibility = View.GONE }
                scheduleLiveCheck()
                maybeAutoCapitalize(ic)
                updateWordSuggestions()
            }
            else -> {
                var code = primaryCode.toChar()
                if (capsOn && !onSymbols) code = code.uppercaseChar()
                ic.commitText(code.toString(), 1)
                updateWordSuggestions()
                justAutoCapped = false
                autoUnshift()
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        super.onDestroy()
        debounceHandler.removeCallbacksAndMessages(null)
    }
}
