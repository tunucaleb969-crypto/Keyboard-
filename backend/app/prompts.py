"""
Prompt templates ported directly from the existing Android AIClient.kt.
Wording is kept identical on purpose — this milestone moves WHERE the
Gemini call happens (server-side), not WHAT is asked of the model.
"""

SINGLE_TASKS = {
    "grammar": (
        "Fix grammar, spelling, punctuation and fluency in the following text. "
        "Preserve the original meaning and tone exactly — only fix errors, don't rewrite style. "
        "Return ONLY the corrected text, nothing else:\n\n{text}"
    ),
    "explain": (
        "Explain what the following sentence means, in simple plain language a beginner "
        "would understand. Keep it short, 1-2 sentences, no jargon:\n\n{text}"
    ),
    "cv": (
        "Rewrite the following text so it sounds professional, achievement-focused, and polished "
        "enough for a CV, resume, or job application. Use strong, active language and remove "
        "filler words. Return ONLY the rewritten text:\n\n{text}"
    ),
    "business": (
        "Rewrite the following text in a clear, confident, respectful business/workplace "
        "tone suitable for a professional email or report. Be direct but courteous, and remove "
        "unnecessary casual phrasing. Return ONLY the rewritten text:\n\n{text}"
    ),
    "livecheck": (
        "TASK: single-word spell check. INPUT is exactly one word, possibly misspelled "
        "or possibly incomplete/an abbreviation. "
        "RULES: Output EXACTLY ONE WORD. Never output a sentence, phrase, or multiple words. "
        "Never output punctuation. Never explain. If the input word is a real, correctly-spelled "
        "English word already (including short/common words like 'ho', 'ok', 'go', 'hi'), output "
        "it back UNCHANGED. Only output a different single word if the input is clearly a typo "
        "of a common English word (like 'wil' -> 'will', 'bac' -> 'back', 'schol' -> 'school'). "
        "OUTPUT FORMAT: exactly one lowercase or as-cased word, nothing else, no period, no quotes.\n\n"
        "INPUT: {text}\nOUTPUT:"
    ),
}

TONE_INSTRUCTIONS = {
    "professional": (
        "Rewrite this in a professional, polished tone suitable for work or business "
        "communication. Use clear, precise language. Avoid slang and casual phrasing."
    ),
    "friendly": (
        "Rewrite this in a warm, friendly, approachable tone, like talking to someone you "
        "like and trust. Keep it natural, not overly formal."
    ),
    "casual": (
        "Rewrite this in a relaxed, casual, conversational tone, like texting a friend. "
        "Contractions and informal phrasing are fine."
    ),
    "formal": (
        "Rewrite this in a formal, respectful tone suitable for official or serious "
        "correspondence. Avoid contractions and slang entirely."
    ),
    "funny": (
        "Rewrite this to be genuinely funny and light-hearted, adding humor or a playful twist "
        "while keeping the core message intact."
    ),
    "flirty": (
        "Rewrite this with a playful, flirty, charming tone — confident and a little teasing, "
        "while staying tasteful."
    ),
    "polite": "Rewrite this to be extra polite and courteous, using considerate, respectful language.",
    "confident": (
        "Rewrite this to sound confident and assertive, direct and self-assured, without "
        "being aggressive or rude."
    ),
}

MULTI_TASKS = {
    "reply": (
        "Suggest 3 short, natural reply options to the following message. Make each option "
        "genuinely different in approach (e.g. one brief, one warmer, one with a follow-up "
        "question). Return exactly 3 lines, one reply per line, no numbering, no extra text:\n\n{text}"
    ),
    "decline": (
        "Suggest 3 short, polite ways to decline or say no to the following message. Vary "
        "the reasoning or warmth between the 3 options. Return exactly 3 lines, one option per "
        "line, no numbering, no extra text:\n\n{text}"
    ),
    "shorten": (
        "Make the following text shorter and more concise while keeping the core meaning. "
        "Give 3 versions of increasing brevity (slightly shorter, much shorter, minimal). Return "
        "exactly 3 lines, one per line, no numbering, no extra text:\n\n{text}"
    ),
    "expand": (
        "Expand the following short text into a fuller, more detailed message, adding "
        "relevant context or detail. Give 3 different expanded versions. Return exactly 3 lines, "
        "one per line, no numbering, no extra text:\n\n{text}"
    ),
    "translate": (
        "Translate the following text into Spanish, French, and German, preserving tone "
        "and meaning as naturally as possible in each language. Return exactly 3 lines in this "
        "order: Spanish, French, German. No labels, no extra text:\n\n{text}"
    ),
}


def build_single_prompt(task: str, text: str) -> str:
    template = SINGLE_TASKS.get(task)
    if template:
        return template.format(text=text)
    return f"Rewrite the following text in a {task} tone. Return ONLY the rewritten text:\n\n{text}"


def build_multi_prompt(task: str, text: str) -> str:
    template = MULTI_TASKS.get(task)
    if template:
        return template.format(text=text)
    instruction = TONE_INSTRUCTIONS.get(task, f"Rewrite this in a {task} tone.")
    return (
        f"{instruction} Give exactly 3 different versions that vary in wording and phrasing, but keep "
        f"the same meaning and roughly the same length as the original. Return exactly 3 lines, "
        f"one version per line, no numbering, no labels, no extra commentary:\n\n{text}"
    )


def parse_multi_result(raw: str) -> list[str]:
    lines = [line.strip().lstrip("-•* ") for line in raw.splitlines()]
    return [line for line in lines if line][:3]
