# AI Keyboard — Project Status

Last updated: 2026-10-04

## Working branch

- Repository: `tunucaleb969-crypto/Keyboard-`
- Implementation branch: `gateway-client`
- Review: https://github.com/tunucaleb969-crypto/Keyboard-/pull/1
- Do not treat the work as released until the PR is reviewed/merged and a successful APK artifact is verified.

## Existing implemented areas (must still be verified on device)

- Android `InputMethodService` keyboard with QWERTY and symbols layouts.
- Typing settings, auto-capitalization, autocorrection, suggestion bar, learned next-word bigrams, dictionary and language-data import/validation.
- Emoji picker/suggestions, clipboard history, themes, sound/vibration, one-handed/layout settings, toolbar and voice-input entry point.
- AI writing operations via a backend gateway, preview/accept/cancel flow, response cache, request cancellation/stale-result guard.
- Sensitive input detection for password/PIN and `IME_FLAG_NO_PERSONALIZED_LEARNING` fields.
- Comma long-press opens Settings; normal tap inserts a comma.

These are code-presence claims, not a claim that every feature has passed real-device testing.

## Changes made in this work session

- CI now runs unit tests before assembling the debug APK and uploads the APK as an artifact.
- AI gateway URL is configurable in Keyboard Settings rather than pointing at a hard-coded placeholder. The app requires an HTTPS URL (emulator loopback is allowed for local testing) and the gateway `APP_SHARED_SECRET`; the Gemini key belongs only on the backend.
- Keyboard AI requests validate missing gateway configuration and oversized text before sending requests.
- Clipboard history no longer saves clipboard content while a sensitive field is focused, and the clipboard listener is removed when the IME service is destroyed.

## Required setup for live AI

1. Deploy `render.yaml` or another ASGI host using `backend/`.
2. Configure `GEMINI_API_KEY` and `APP_SHARED_SECRET` in the host's secret/environment settings. Never commit either secret.
3. Copy the deployed gateway's actual HTTPS service URL into **Keyboard Settings → AI Connection → AI gateway URL**.
4. Enter the same `APP_SHARED_SECRET` into the gateway app-key field.
5. Verify `/healthz`, authenticated API calls, and AI actions with a live deployment. A configured URL is not proof of a live deployment.

## Validation status

- GitHub Actions has been configured to run `gradle test --no-daemon` and `gradle assembleDebug --no-daemon` on PRs and relevant pushes.
- Check the live CI run and APK artifact before calling the current revision build-verified: https://github.com/tunucaleb969-crypto/Keyboard-/actions
- Real-device checks remain required for IME behavior, keyboard switching, field types, clipboard permissions/behavior, voice input, themes, accessibility, latency, and OEM-specific behavior.

## Known gaps / next implementation milestones

1. Confirm the latest unit-test and APK-build runs; fix every reproducible failure.
2. Test the live gateway end-to-end. The Android app must not claim AI is online merely because a URL is saved.
3. Expand provider routing beyond Gemini behind the existing provider interface; do not expose provider secrets in Android.
4. Improve local prediction/correction quality with measured test cases for `pqste → paste`, `im → I'm`, capitalization, punctuation, Unicode, and false-positive avoidance.
5. Add regression tests for sensitive fields, AI preview/cancel/stale results, learned bigrams, dictionary imports, and URL/config validation.
6. Verify that AI/emoji/tone/clipboard workspaces occupy the key area and do not create a duplicate keyboard or displace the whole IME.
7. Audit accessibility, privacy/retention controls, crash safety, cold-start latency, memory/battery use, and multilingual behavior on real devices.

## Definition of done

- Unit tests pass and debug APK assembles in CI.
- APK artifact is downloadable and opens/installs on a test device.
- Core typing/editing and sensitive-field protections pass manual regression checks.
- Every visible tool either works or is clearly unavailable; no placeholder URLs or fake success states remain.
- AI gateway is deployed and tested with secrets stored server-side only.
- README and this file match the actual state; remaining gaps are explicitly listed.
