package com.kwame.aikeyboard.language

/** One rejected input line, with why it was rejected — so import problems are visible, not silent. */
data class RejectedRow(val lineNumber: Int, val rawLine: String, val reason: String)

/** Outcome of importing a single source file. */
data class ImportFileResult(
    val fileName: String,
    val rowsRead: Int,
    val rowsInserted: Int,
    val rowsUpdated: Int,
    val rowsSkippedDuplicate: Int,
    val rowsRejected: Int,
    val rejections: List<RejectedRow>,
    val skippedUnchanged: Boolean
)

/** Outcome of a full import run across every known source file. */
data class ImportSummary(val fileResults: List<ImportFileResult>) {
    val totalRowsInserted: Int get() = fileResults.sumOf { it.rowsInserted }
    val totalRowsRejected: Int get() = fileResults.sumOf { it.rowsRejected }
}
