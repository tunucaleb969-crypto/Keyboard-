            package com.kwame.aikeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AIKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard
    private val scope = CoroutineScope(Dispatchers.Main)
    private var pendingSuggestJob: Job? = null
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var capsOn = false

    private lateinit var wordBtn1: Button
    private lateinit var wordBtn2: Button
    private lateinit var wordBtn3: Button
    private val wordButtons by lazy { listOf(wordBtn1, wordBtn2, wordBtn3) }

    private val aiClient: AIClient
        get() = AIClient(Prefs.getApiKey(this))

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_container, null)
        keyboardView = root.findViewById(R.id.keyboardView)
        keyboard = Keyboard(this, R.xml.keyboard_qwerty)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)

        wordBtn1 = root.findViewById(R.id.wordSuggest1)
        wordBtn2 = root.findViewById(R.id.wordSuggest2)
        wordBtn3 = root.findViewById(R.id.wordSuggest3)
        wordButtons.forEach { btn ->
            btn.setOnClickListener { insertSuggestedWord(btn.text.toString()) }
        }

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

        return root
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
                    ic.beginBatchEdit()
                    ic.deleteSurroundingText(before.length, after.length)
                    ic.commitText(result, 1)
                    ic.endBatchEdit()
                } else {
                    showReplyOptions(result)
                }
            }.onFailure {
                toast("AI request failed: ${it.message}")
            }
        }
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
                // Real build: call aiClient.run("grammar", before) here and surface a
                // non-destructive suggestion chip instead of auto-replacing.
            }
        }, 900)
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection = currentInputConnection ?: return
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
                updateWordSuggestions()
            }
            -1 -> {
                capsOn = !capsOn
                keyboard.isShifted = capsOn
                keyboardView.invalidateAllKeys()
            }
            -2 -> { /* symbols toggle — extend with a second Keyboard xml for numbers/symbols */ }
            10 -> {
                ic.commitText("\n", 1)
                wordButtons.forEach { it.visibility = View.GONE }
            }
            32 -> {
                ic.commitText(" ", 1)
                wordButtons.forEach { it.visibility = View.GONE }
                scheduleLiveCheck()
            }
            else -> {
                var code = primaryCode.toChar()
                if (capsOn) code = code.uppercaseChar()
                ic.commitText(code.toString(), 1)
                updateWordSuggestions()
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
