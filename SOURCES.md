# Language Data Sources

Documents where every piece of bundled language data came from, per the project's
licensing requirements. Nothing may be added here without a license permitting this use,
and nothing here was copied from Gboard or any other proprietary keyboard.

## Current seed/test data (Phase 3)

| File | Origin | License | Notes |
|---|---|---|---|
| words_seed.tsv | Hand-curated by Claude (Anthropic) from general knowledge, for testing only | N/A — original text written for this project | ~142 entries: common English function words, a few technical/computing terms, real place names (incl. Ghanaian cities), real personal names, common slang/abbreviations. Frequencies are illustrative rankings, not measured corpus counts. |
| corrections_seed.tsv | Hand-curated by Claude, for testing only | N/A | ~10 well-known common English typos (teh/the, recieve/receive, etc.) |
| bigrams_seed.tsv | Hand-curated by Claude, for testing only | N/A | ~10 common word-pairs |
| emoji_seed.tsv | Hand-curated by Claude, for testing only | N/A | ~6 word→emoji associations |
| phrases_seed.tsv | Hand-curated by Claude, for testing only | N/A | ~5 common multi-word phrases |

**This is test/seed data only — a few hundred entries total, not a production
dictionary.** It exists to prove the import pipeline works end-to-end.

## Planned production datasets (not yet imported)

| Dataset | License | Status |
|---|---|---|
| SCOWL / en-wl wordlist | Permissive (public-domain-ish BSD/MIT-style combined license) | Not yet obtained — needs to be downloaded and converted to words_seed.tsv format by the developer, since Claude's working environment cannot bulk-download external files |
| GeoNames | CC BY 4.0 (attribution required) | Same — pending manual download/conversion |
| Unicode CLDR emoji annotations | Unicode License (permissive) — not independently re-verified this session, verify before use | Same |

When one of these is actually imported, add a row above with the real date obtained,
exact source URL, and exact license text/version used — replace this placeholder note
with real facts at that time.
