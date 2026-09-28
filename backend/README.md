# AI Keyboard Gateway (Milestone 1)

A backend between the Android keyboard and Gemini, so the Gemini API key
never lives in the app. Same prompt behavior as the old client-side
`AIClient.kt`: this milestone moves *where* the call happens, not what it
asks the model to do.

## What this is NOT yet
No model router, no multiple providers, no teacher system, no training
pipeline. Those are later milestones once this foundation is in place.

## Endpoints
- `POST /api/v1/complete` - `{task, text}` -> `{result, provider, model}`
  (grammar, explain, cv, business, livecheck)
- `POST /api/v1/suggest` - `{task, text}` -> `{results: [...], provider, model}`
  (reply, decline, shorten, expand, translate, or any tone name)
- `GET /api/v1/models` - registry info for the active provider
- `GET /api/v1/health` - `{status, provider_reachable}`

All routes require header `X-App-Key: <APP_SHARED_SECRET>`.

## Run locally
```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env   # then fill in GEMINI_API_KEY and APP_SHARED_SECRET
export $(grep -v '^#' .env | xargs)
uvicorn app.main:app --reload --port 8000
```

## Run the tests
```bash
pip install -r requirements-dev.txt
pytest -v
```

## Verification status (honest accounting)
- VERIFIED: every file is syntax-valid (`python3 -m py_compile`).
- VERIFIED: the ported prompt logic (`app/prompts.py`) was unit tested and
  matches the structure of the original Kotlin `AIClient.kt`.
- NOT YET VERIFIED: a live server run and `pytest` results. The environment
  that authored this code had no network access, so dependencies could not
  be installed. Run the tests above before deploying; treat this code as
  INSPECTED, not tested, until they pass.

## Deploying
Any host that runs a Python ASGI app works (Render, Fly.io, Railway, a VPS).
Set `GEMINI_API_KEY` and `APP_SHARED_SECRET` as environment variables or
secrets on that host, never in code and never in the Android app.

## Known limitations (by design for this milestone)
- `APP_SHARED_SECRET` authenticates "this is our app," not "this is user X."
  Move to per-user auth before real user growth.
- Rate limiting is in-memory and single-process. Use a shared store (e.g.
  Redis) before running multiple instances.
