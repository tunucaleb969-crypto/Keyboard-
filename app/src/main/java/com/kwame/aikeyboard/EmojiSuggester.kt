package com.kwame.aikeyboard

object EmojiSuggester {
    val commonEmojis = listOf(
        "😀","😂","😊","😍","😘","😉","😎","🤔","😢","😭",
        "😡","😴","🥳","😱","🙄","😇","🤗","🙏","👍","👎",
        "👏","🙌","💪","✌️","🤝","❤️","💔","🔥","✨","🎉",
        "💯","👀","🙈","😅","🤷","💀","🎂","☕","🍕","⚽",
        "📱","💻","🕐","📅","✅","❌","💬","📩","😴","🥺"
    )

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

    // Maps each emoji to a list of searchable keywords, for the emoji search box.
    private val emojiKeywords = mapOf(
        "😀" to listOf("grin", "happy", "smile"),
        "😂" to listOf("laugh", "lol", "funny", "tears"),
        "😊" to listOf("happy", "smile", "blush"),
        "😍" to listOf("love", "heart eyes", "crush"),
        "😘" to listOf("kiss", "love"),
        "😉" to listOf("wink"),
        "😎" to listOf("cool", "sunglasses"),
        "🤔" to listOf("think", "hmm", "wonder"),
        "😢" to listOf("sad", "cry", "tear"),
        "😭" to listOf("cry", "sob", "sad"),
        "😡" to listOf("angry", "mad", "rage"),
        "😴" to listOf("sleep", "tired", "zzz"),
        "🥳" to listOf("party", "celebrate", "excited"),
        "😱" to listOf("shock", "scared", "surprised"),
        "🙄" to listOf("eyeroll", "annoyed"),
        "😇" to listOf("angel", "innocent"),
        "🤗" to listOf("hug"),
        "🙏" to listOf("thanks", "please", "pray"),
        "👍" to listOf("thumbs up", "yes", "ok", "good"),
        "👎" to listOf("thumbs down", "no", "bad"),
        "👏" to listOf("clap", "great", "applause"),
        "🙌" to listOf("celebrate", "praise", "hands up"),
        "💪" to listOf("strong", "muscle", "flex"),
        "✌️" to listOf("peace", "victory"),
        "🤝" to listOf("handshake", "deal", "agree"),
        "❤️" to listOf("love", "heart"),
        "💔" to listOf("heartbreak", "sad", "breakup"),
        "🔥" to listOf("fire", "hot", "lit"),
        "✨" to listOf("sparkle", "amazing", "shine"),
        "🎉" to listOf("party", "celebrate", "congrats"),
        "💯" to listOf("hundred", "perfect"),
        "👀" to listOf("eyes", "look", "watching"),
        "🙈" to listOf("shy", "embarrassed", "monkey"),
        "😅" to listOf("sweat", "nervous laugh"),
        "🤷" to listOf("shrug", "idk"),
        "💀" to listOf("dead", "skull", "dying laughing"),
        "🎂" to listOf("birthday", "cake"),
        "☕" to listOf("coffee", "tea"),
        "🍕" to listOf("pizza", "food"),
        "⚽" to listOf("soccer", "football", "sport"),
        "📱" to listOf("phone", "mobile"),
        "💻" to listOf("laptop", "computer"),
        "🕐" to listOf("clock", "time"),
        "📅" to listOf("calendar", "date"),
        "✅" to listOf("check", "done", "yes"),
        "❌" to listOf("cross", "no", "wrong"),
        "💬" to listOf("chat", "message", "speech"),
        "📩" to listOf("mail", "email", "message"),
        "🥺" to listOf("pleading", "puppy eyes", "please")
    )

    fun searchEmojis(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return commonEmojis.filter { emoji ->
            emojiKeywords[emoji]?.any { it.contains(q) } == true
        }
    }
}
