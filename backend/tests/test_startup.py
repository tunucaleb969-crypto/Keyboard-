"""
Startup tests: exercise the REAL provider and the REAL startup handler.

Why this file exists: test_api.py uses TestClient(app) without a `with` block,
which never fires the app's startup event, and it replaces the provider with a
fake. So nothing in CI ever constructed the real GeminiProvider or ran the
startup code that Render runs when it launches `uvicorn app.main:app`.
"""
import os

os.environ.setdefault("APP_SHARED_SECRET", "test-secret")
os.environ.setdefault("GEMINI_API_KEY", "test-key-not-real")

from fastapi.testclient import TestClient  # noqa: E402

from app.main import app, app_state  # noqa: E402
from app.providers.gemini import GeminiProvider  # noqa: E402


def test_real_gemini_provider_can_be_constructed():
    provider = GeminiProvider(api_key="not-a-real-key", model="gemini-3.1-flash-lite")
    assert provider.get_model_info() == {"provider": "gemini", "model": "gemini-3.1-flash-lite"}


def test_app_startup_and_shutdown_run_cleanly_and_healthz_answers():
    # `with` makes TestClient run the startup and shutdown handlers, exactly as uvicorn does.
    with TestClient(app) as started_client:
        assert isinstance(app_state["provider"], GeminiProvider)
        response = started_client.get("/healthz")
        assert response.status_code == 200
        assert response.json() == {"status": "ok"}
