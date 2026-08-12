package com.kwame.aikeyboard

object EmojiSuggester {
    // A grid of commonly used emojis for the picker panel.
    val commonEmojis = listOf(
        "😀","😂","😊","😍","😘","😉","😎","🤔","😢","😭",
        "😡","😴","🥳","😱","🙄","😇","🤗","🙏","👍","👎",
        "👏","🙌","💪","✌️","🤝","❤️","💔","🔥","✨","🎉",
        "💯","👀","🙈","😅","🤷","💀","🎂","☕","🍕","⚽",
        "📱","💻","🕐","📅","✅","❌","💬","📩","😴","🥺"
    )

    // Maps common words to a fitting emoji, for suggestion-while-typing.
    private val wordToEmoji = mapOf(
        "happy" to "😊", "sad" to "😢", "love" to "❤️", "laugh" to "😂",
        "funny" to "😂", "angry" to "😡", "tired" to "😴", "excited" to "🥳",
        "cry" to "😭", "party" to "🎉", "fire" to "🔥", "cool" to "😎",
        "thanks" to "🙏", "thank" to "🙏", "please" to "🙏", "sorry" to "😔",
        "congrats" to "🎉", "congratulations" to "🎉", "birthday" to "🎂",
        "food" to "🍕", "coffee" to "☕", "yes" to "✅", "no" to "❌",
        "ok" to "👍", "okay" to "👍", "good" to "👍", "great" to "👏",
        "amazing" to "✨", "wow" to "😱", "shock" to "😱", "shocked" to "😱",
        "sleep" to "😴", "sleepy" to "😴", "strong" to "💪", "win" to "🏆",
        "lol" to "😂", "haha" to "😂", "hello" to "👋", "hi" to "👋",
        "bye" to "👋", "goodbye" to "👋", "phone" to "📱", "call" to "📞",
        "time" to "🕐", "today" to "📅", "message" to "💬", "hot" to "🔥"
    )

    fun suggestForWord(word: String): String? = wordToEmoji[word.lowercase()]
}
