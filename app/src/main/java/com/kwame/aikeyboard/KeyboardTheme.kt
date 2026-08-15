package com.kwame.aikeyboard

/** A set of colors that define one keyboard theme. */
data class KeyboardTheme(
    val id: String,
    val displayName: String,
    val background: Int,
    val keyBackground: Int,
    val keyBackgroundPressed: Int,
    val textColor: Int,
    val accentColor: Int
)

object KeyboardThemes {
    val all = listOf(
        KeyboardTheme("indigo", "Indigo", 0xFF14142B.toInt(), 0xFF2A2A45.toInt(), 0xFF5B4FE8.toInt(), 0xFFFFFFFF.toInt(), 0xFF7C5CFC.toInt()),
        KeyboardTheme("dark", "Dark", 0xFF000000.toInt(), 0xFF1E1E1E.toInt(), 0xFF3A3A3A.toInt(), 0xFFFFFFFF.toInt(), 0xFF4A90E2.toInt()),
        KeyboardTheme("light", "Light", 0xFFF5F5F5.toInt(), 0xFFFFFFFF.toInt(), 0xFFE0E0E0.toInt(), 0xFF000000.toInt(), 0xFF4A90E2.toInt()),
        KeyboardTheme("sky", "Sky", 0xFFB3E5FC.toInt(), 0xFF81D4FA.toInt(), 0xFF4FC3F7.toInt(), 0xFF01579B.toInt(), 0xFF0288D1.toInt()),
        KeyboardTheme("red", "Red", 0xFFB71C1C.toInt(), 0xFFD32F2F.toInt(), 0xFFEF5350.toInt(), 0xFFFFFFFF.toInt(), 0xFFFF8A80.toInt()),
        KeyboardTheme("green", "Green", 0xFF66BB6A.toInt(), 0xFF81C784.toInt(), 0xFFA5D6A7.toInt(), 0xFF1B5E20.toInt(), 0xFF2E7D32.toInt()),
        KeyboardTheme("purple", "Purple", 0xFF4527A0.toInt(), 0xFF5E35B1.toInt(), 0xFF7E57C2.toInt(), 0xFFFFFFFF.toInt(), 0xFFB388FF.toInt()),
        KeyboardTheme("cream", "Cream", 0xFFEFEBE9.toInt(), 0xFFF5F0EC.toInt(), 0xFFE0D8D0.toInt(), 0xFF3E2723.toInt(), 0xFF8D6E63.toInt()),
        KeyboardTheme("pink", "Pink", 0xFFF8BBD0.toInt(), 0xFFF48FB1.toInt(), 0xFFF06292.toInt(), 0xFF880E4F.toInt(), 0xFFEC407A.toInt()),
        KeyboardTheme("maroon", "Maroon", 0xFF880E4F.toInt(), 0xFFAD1457.toInt(), 0xFFC2185B.toInt(), 0xFFFFFFFF.toInt(), 0xFFFF80AB.toInt()),
        KeyboardTheme("teal", "Teal", 0xFF00695C.toInt(), 0xFF00796B.toInt(), 0xFF00897B.toInt(), 0xFFFFFFFF.toInt(), 0xFF80CBC4.toInt()),
        KeyboardTheme("brown", "Brown", 0xFF3E2723.toInt(), 0xFF4E342E.toInt(), 0xFF6D4C41.toInt(), 0xFFFFFFFF.toInt(), 0xFFBCAAA4.toInt())
    )

    fun getById(id: String): KeyboardTheme = all.firstOrNull { it.id == id } ?: all.first()
}
