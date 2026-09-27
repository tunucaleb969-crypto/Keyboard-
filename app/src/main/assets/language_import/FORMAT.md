# Language Data Import Format

This describes the exact file format the importer (`LanguageDataImporter.kt`) expects.
Files live in `app/src/main/assets/language_import/` and are plain UTF-8 text,
tab-separated (TSV), one entry per line. Lines starting with `#` (after trimming
whitespace) or blank lines are ignored — use `#` for comments/headers.

The importer is safe to re-run: each file's exact content is hashed (SHA-256), and if a
file's hash hasn't changed since the last import, it's skipped entirely. Only changed or
new files are re-processed, so adding a few hundred new words does not require
re-importing the whole dataset.

## words_seed.tsv → `words` table
```
word<TAB>frequency<TAB>category<TAB>region(optional)
```
- **word**: letters (any script), apostrophes, hyphens only. No spaces, no digits.
  Multi-word entries go in `phrases_seed.tsv` instead. Max 45 characters.
- **frequency**: non-negative integer. Higher = suggested/ranked first. Doesn't need to
  be a "real" corpus count — relative ordering is what matters.
- **category**: one of `common`, `name`, `place`, `technical`, `abbreviation`, `slang`.
- **region** (optional): free-text tag such as `US`, `UK`, `GH` — not currently used for
  filtering, reserved for future locale-aware suggestions.

Existing (word, category) pairs are updated in place (frequency can change) — this is
not append-only.

## corrections_seed.tsv → `corrections` table
```
misspelling<TAB>correction<TAB>confidence(optional, 0.0-1.0, default 1.0)
```
misspelling and correction must differ (case-insensitively). This table is for
**known, bundled** typo→correction pairs — separate from, and a supplement to, the
general correction algorithm (edit distance/keyboard-proximity) built in the
autocorrection add-on.

## bigrams_seed.tsv → `bigrams` table
```
previous_word<TAB>next_word<TAB>frequency(optional, default 1)
```
Used for next-word prediction. Same word rules as words_seed.tsv apply to both columns.

## emoji_seed.tsv → `emoji_associations` table
```
word<TAB>emoji
```
One emoji per line; a word can have multiple lines (multiple emoji).

## phrases_seed.tsv → `phrases` table
```
phrase<TAB>frequency(optional, default 0)
```
Must contain at least two space-separated words. Max 200 characters.

## What gets rejected
Any line that fails validation is skipped (not the whole file) and recorded in that
file's `ImportFileResult.rejections` with a reason and line number. Malformed lines
never crash the import or corrupt the database — they're just excluded.

## Adding a large external dataset
1. Convert your source data to one of the five TSV formats above (a spreadsheet
   exported as tab-separated text works fine).
2. Name it to match one of the five file names above, or extend
   `LanguageDataImporter.kt`'s file list if you're adding a genuinely new source file.
3. Put it in `app/src/main/assets/language_import/`.
4. Document it in `SOURCES.md` (dataset name, source, license, date obtained).
5. Commit and rebuild — the importer picks it up automatically on next app start.

## Known limitation for very large files
This importer reads an entire file's lines into memory at once (`bufferedReader().use(BufferedReader::readLines)`)
before processing. This is fine for files up to at least tens of thousands of lines on
a modern phone, but has NOT been tested or benchmarked against a multi-million-row file.
If a future production dataset gets that large, this method should be changed to stream
line-by-line instead of loading the whole file into a List<String> first — flagging this
now rather than pretending it's already handled.
