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
    private var lastSpaceTime = 0L

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

    // For "Undo autocorrect": remembers the last auto-correction so backspace right after can revert it.
    private var lastCorrectionOriginal: String = ""
    private var lastCorrectionResult: String = ""

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

        updateEnterKeyLabel()

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

    private fun runAiOnFullText(task: String, replaceText: Boolean = true, showInPreviewOnly: Boolean = false) {
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

        scope.launch {
            aiClient.run(task, fullText).onSuccess { result ->
                when {
                    showInPreviewOnly -> {
                        pendingBefore = ""
                        pendingAfter = ""
                        pendingResult = ""
                        showPreview(result)
                    }
                    replaceText -> {
                        pendingBefore = before
                        pendingAfter = after
                        pendingResult = result
                        showPreview(result)
                    }
                    else -> showReplyOptions(result)
                }
            }.onFailure {
                toast("AI request failed: ${it.message}")
            }
        }
    }

    private fun runAiMulti(task: String, replaceText: Boolean = true) {
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

        scope.launch {
            aiClient.runMulti(task, fullText).onSuccess { options ->
                if (options.isEmpty()) {
                    toast("No suggestions came back — try again")
                    return@onSuccess
                }
                multiBefore = before
                multiAfter = after
                multiReplaces = replaceText
                showMultiPanel(options)
            }.onFailure {
                toast("AI request failed: ${it.message}")
            }
        }
    }

    private fun showMultiPanel(options: List<String>) {
        val buttons = listOf(option1, option2, option3)
        buttons.forEachIndexed { index, btn ->
            val text = options.getOrNull(index)
            if (text != null) {
                btn.text = text
                btn.visibility = View.VISIBLE
                btn.setOnClickListener { chooseMultiOption(text) }
            } else {
                btn.visibility = View.GONE
            }
        }
        multiPanel.visibility = View.VISIBLE
    }

    private fun chooseMultiOption(text: String) {
        val ic = currentInputConnection ?: return
        if (multiReplaces) {
            ic.beginBatchEdit()
            ic.deleteSurroundingText(multiBefore.length, multiAfter.length)
            ic.commitText(text, 1)
            ic.endBatchEdit()
        } else {
            ic.commitText(text, 1)
        }
        hideMultiPanel()
    }

    private fun hideMultiPanel() {
        multiPanel.visibility = View.GONE
        multiBefore = ""
        multiAfter = ""
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
        if (!Prefs.getWordSuggestionsEnabled(this)) {
            wordButtons.forEach { it.visibility = View.GONE }
            emojiSuggestBtn.visibility = View.GONE
            return
        }
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

    private val ignoredWords = mutableSetOf<String>()

    private fun checkLastWordNow() {
        if (!Prefs.getAutoCorrectEnabled(this)) return
        val ic = currentInputConnection ?: return
        if (Prefs.getApiKey(this).isBlank()) return

        val fullBefore = ic.getTextBeforeCursor(60, 0)?.toString().orEmpty()
        val trimmed = fullBefore.trimEnd()
        val word = trimmed.substringAfterLast(" ").substringAfterLast("\n")

        if (word.length < 2) return
        if (word.lowercase() in ignoredWords) return
        if (!word.any { it.isLetter() }) return
        if (WordSuggester.isKnownWord(word)) return

        pendingSuggestJob?.cancel()
        pendingSuggestJob = scope.launch {
            aiClient.run("livecheck", word).onSuccess { result ->
                val corrected = result.trim()
                if (corrected.isBlank() || corrected.equals("NONE", ignoreCase = true)) return@onSuccess
                if (corrected.equals(word, ignoreCase = true)) return@onSuccess
                if (corrected.contains(" ")) return@onSuccess

                val currentIc = currentInputConnection ?: return@onSuccess
                val currentBefore = currentIc.getTextBeforeCursor(60, 0)?.toString().orEmpty()
                if (!currentBefore.trimEnd().endsWith(word)) return@onSuccess
                if (!currentBefore.endsWith("$word ")) return@onSuccess

                currentIc.beginBatchEdit()
                currentIc.deleteSurroundingText(word.length + 1, 0)
                currentIc.commitText("$corrected ", 1)
                currentIc.endBatchEdit()
                ignoredWords.add(word.lowercase())
                lastCorrectionOriginal = word
                lastCorrectionResult = corrected
            }.onFailure { }
        }
    }

    /** If "Undo autocorrect" is on and the user backspaces right after a correction, revert it. */
    private fun tryUndoAutocorrect(ic: InputConnection): Boolean {
        if (!Prefs.getUndoAutocorrectEnabled(this)) return false
        if (lastCorrectionResult.isBlank()) return false
        val before = ic.getTextBeforeCursor(60, 0)?.toString().orEmpty()
        if (!before.trimEnd().endsWith(lastCorrectionResult)) return false

        ic.beginBatchEdit()
        ic.deleteSurroundingText(lastCorrectionResult.length, 0)
        ic.commitText(lastCorrectionOriginal, 1)
        ic.endBatchEdit()
        ignoredWords.add(lastCorrectionOriginal.lowercase())
        lastCorrectionOriginal = ""
        lastCorrectionResult = ""
        return true
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

    private fun playKeyFeedback(isRepeatedAction: Boolean = false) {
        if (Prefs.getSoundEnabled(this)) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
        val vibrateAllowed = if (isRepeatedAction) Prefs.getRepeatedVibrateEnabled(this) else Prefs.getVibrateEnabled(this)
        if (vibrateAllowed) {
            val duration = Prefs.getVibrateDuration(this).toLong().coerceAtLeast(1)
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, 255))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
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

    /** Handles the space bar: double-space-period and normal space + auto-capitalize check. */
    private fun handleSpace(ic: InputConnection) {
        if (Prefs.getDoubleSpacePeriodEnabled(this)) {
            val now = System.currentTimeMillis()
            val before = ic.getTextBeforeCursor(1, 0)?.toString().orEmpty()
            if (before == " " && (now - lastSpaceTime) < 500) {
                ic.deleteSurroundingText(1, 0)
                ic.commitText(". ", 1)
                lastSpaceTime = 0L
                wordButtons.forEach { it.visibility = View.GONE }
                maybeAutoCapitalize(ic)
                return
            }
            lastSpaceTime = now
        }
        ic.commitText(" ", 1)
        wordButtons.forEach { it.visibility = View.GONE }
        checkLastWordNow()
        maybeAutoCapitalize(ic)
        updateWordSuggestions()
    }

    /** Handles punctuation keys: optionally auto-inserts a space after . , ! ? */
    private fun handlePunctuation(ic: InputConnection, char: Char) {
        ic.commitText(char.toString(), 1)
        if (Prefs.getAutoSpacePunctuationEnabled(this) && char in ".,!?") {
            ic.commitText(" ", 1)
            maybeAutoCapitalize(ic)
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
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                playKeyFeedback(isRepeatedAction = true)
                if (!tryUndoAutocorrect(ic)) {
                    val selected = ic.getSelectedText(0)
                    if (!selected.isNullOrEmpty()) {
                        ic.commitText("", 1)
                    } else {
                        ic.deleteSurroundingText(1, 0)
                    }
                }
                updateWordSuggestions()
            }
            -1 -> {
                playKeyFeedback()
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
            -2 -> {
                playKeyFeedback()
                toggleSymbols()
            }
            10 -> {
                playKeyFeedback()
                val handled = when (currentImeAction) {
                    EditorInfo.IME_ACTION_SEARCH,
                    EditorInfo.IME_ACTION_SEND,
                    EditorInfo.IME_ACTION_GO,
                    EditorInfo.IME_ACTION_DONE,
                    EditorInfo.IME_ACTION_NEXT -> {
                        ic.performEditorAction(currentImeAction)
                        true
                    }
                    else -> false
                }
                if (!handled) {
                    ic.commitText("\n", 1)
                    wordButtons.forEach { it.visibility = View.GONE }
                    maybeAutoCapitalize(ic)
                }
            }
            32 -> {
                playKeyFeedback()
                handleSpace(ic)
            }
            44, 46, 33, 63 -> { // , . ! ?
                playKeyFeedback()
                handlePunctuation(ic, primaryCode.toChar())
                updateWordSuggestions()
                justAutoCapped = false
                autoUnshift()
            }
            else -> {
                playKeyFeedback()
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

    private var currentImeAction: Int = EditorInfo.IME_ACTION_NONE

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentImeAction = attribute?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        updateEnterKeyLabel()
    }

    /** Changes the Enter key's visible label to match what the current app actually wants (Send, Search, etc). */
    private fun updateEnterKeyLabel() {
        if (!::qwertyKeyboard.isInitialized) return
        val label = when (currentImeAction) {
            EditorInfo.IME_ACTION_SEARCH -> "Search"
            EditorInfo.IME_ACTION_SEND -> "Send"
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_DONE -> "Done"
            EditorInfo.IME_ACTION_NEXT -> "Next"
            else -> "↵"
        }
        qwertyKeyboard.keys.firstOrNull { it.codes.isNotEmpty() && it.codes[0] == 10 }?.let { key ->
            key.label = label
        }
        if (::keyboardView.isInitialized) {
            keyboardView.invalidateAllKeys()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        debounceHandler.removeCallbacksAndMessages(null)
    }
}
