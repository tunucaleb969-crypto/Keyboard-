# AI Keyboard

An Android input-method keyboard with local typing assistance and optional AI writing tools backed by a small server-side gateway.

## Current implementation

- Android IME with QWERTY and symbols layouts.
- Local word suggestions, learned next-word associations, autocorrection, dictionary/language-data import, emoji suggestions and clipboard history.
- Themes, typing, sound/vibration, layout, toolbar and voice-input settings.
- AI writing actions routed through `backend/`; provider credentials stay on the server.
- Automated Kotlin and backend tests, plus a GitHub Actions debug-APK build.

See [`PROJECT_STATUS.md`](PROJECT_STATUS.md) for verified-vs-unverified status and remaining milestones. Features listed here still require real-device checks before being described as production-ready.

## Build and test

The repository includes a Gradle wrapper configuration. In an Android/Java 17 environment with network access:

```bash
gradle test --no-daemon
gradle assembleDebug --no-daemon
```

The debug APK is expected at `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions uploads it as the `ai-keyboard-debug-apk` artifact when the workflow succeeds.

Backend tests:

```bash
cd backend
python -m pip install -r requirements-dev.txt
pytest -v
```

## Configure the AI gateway

1. Deploy the `backend/` service using `render.yaml` or another ASGI host.
2. Set `GEMINI_API_KEY` and `APP_SHARED_SECRET` in the host's secret/environment configuration. Do not put the Gemini key in Android or commit either secret.
3. In the app's **Settings → AI Connection**, enter the actual deployed HTTPS gateway URL and the matching `APP_SHARED_SECRET` value.
4. Verify the deployment's `/healthz` endpoint, then test an authenticated AI action. Saving a URL alone does not prove the server is live.

For local Android emulator development, the gateway URL validator permits `http://10.0.2.2:8000`; public deployments must use HTTPS.

## Privacy notes

- Password/PIN fields and fields marked `IME_FLAG_NO_PERSONALIZED_LEARNING` disable AI requests and word learning.
- Clipboard history is not updated while a sensitive field is focused, and the service removes its clipboard listener when destroyed.
- The Android app must never contain the Gemini provider key. The gateway shared secret is a basic app-to-gateway gate, not per-user identity; review `backend/README.md` before exposing a public service broadly.

## Project status

Work is in progress on the `gateway-client` branch and is tracked in [PR #1](https://github.com/tunucaleb969-crypto/Keyboard-/pull/1). Do not treat it as merged or release-ready until CI passes, the APK artifact is inspected/installed, and the required manual IME and live-gateway checks are complete.
