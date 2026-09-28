"""
Automated tests, meant to run in CI (GitHub Actions) where pip installs and
network access work — the sandbox that authored this code had neither, so
this file is what turns "INSPECTED" code into "VERIFIED" code.

The Gemini provider is monkeypatched so these tests are deterministic and
don't depend on a real API key or real network access — they check our
gateway's own logic (auth, rate limiting, prompt building, error handling),
not Gemini's behavior.
"""
import os

os.environ.setdefault("APP_SHARED_SECRET", "test-secret")
os.environ.setdefault("GEMINI_API_KEY", "test-key-not-real")

from fastapi.testclient import TestClient  # noqa: E402

from app.main import app, app_state  # noqa: E402
from app.providers.base import ProviderError  # noqa: E402

client = TestClient(app)
HEADERS = {"X-App-Key": "test-secret"}


class FakeProvider:
    """Stands in for GeminiProvider so tests don't need real network/keys."""

    def __init__(self, response: str = "corrected text", fail: bool = False):
        self._response = response
        self._fail = fail
        self.calls = 0

    async def generate(self, prompt: str, max_output_tokens: int = 500) -> str:
        self.calls += 1
        if self._fail:
            raise ProviderError("simulated provider failure")
        return self._response

    async def health_check(self) -> bool:
        self.calls += 1
        return not self._fail

    def get_capabilities(self) -> dict:
        return {"streaming": False, "multi_result": True, "tasks": ["grammar"]}

    def get_model_info(self) -> dict:
        return {"provider": "gemini", "model": "gemini-3.1-flash-lite"}


def setup_function():
    app_state["provider"] = FakeProvider()


def test_missing_app_key_is_rejected():
    response = client.post("/api/v1/complete", json={"task": "grammar", "text": "i has a apple"})
    assert response.status_code == 401


def test_wrong_app_key_is_rejected():
    response = client.post(
        "/api/v1/complete",
        json={"task": "grammar", "text": "i has a apple"},
        headers={"X-App-Key": "wrong"},
    )
    assert response.status_code == 401


def test_complete_success():
    app_state["provider"] = FakeProvider(response="I have an apple.")
    response = client.post(
        "/api/v1/complete", json={"task": "grammar", "text": "i has a apple"}, headers=HEADERS
    )
    assert response.status_code == 200
    body = response.json()
    assert body["result"] == "I have an apple."
    assert body["provider"] == "gemini"


def test_complete_provider_failure_returns_502_not_a_crash():
    app_state["provider"] = FakeProvider(fail=True)
    response = client.post(
        "/api/v1/complete", json={"task": "grammar", "text": "hello"}, headers=HEADERS
    )
    assert response.status_code == 502


def test_suggest_parses_three_lines():
    app_state["provider"] = FakeProvider(response="option one\noption two\noption three")
    response = client.post(
        "/api/v1/suggest", json={"task": "reply", "text": "want to grab lunch?"}, headers=HEADERS
    )
    assert response.status_code == 200
    assert response.json()["results"] == ["option one", "option two", "option three"]


def test_health_reports_provider_status():
    app_state["provider"] = FakeProvider(fail=True)
    response = client.get("/api/v1/health", headers=HEADERS)
    assert response.status_code == 200
    assert response.json()["provider_reachable"] is False


def test_healthz_needs_no_auth_and_never_calls_the_provider():
    # Render's health probe: must work without X-App-Key and must not spend
    # Gemini quota, or every probe would cost a real API call.
    fake = FakeProvider()
    app_state["provider"] = fake
    response = client.get("/healthz")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
    assert fake.calls == 0


def test_rate_limit_blocks_after_configured_max(monkeypatch):
    monkeypatch.setattr("app.rate_limit.settings.rate_limit_per_minute", 2)
    from app.rate_limit import _hits
    _hits.clear()
    app_state["provider"] = FakeProvider()
    for _ in range(2):
        r = client.post("/api/v1/complete", json={"task": "grammar", "text": "hi"}, headers=HEADERS)
        assert r.status_code == 200
    r = client.post("/api/v1/complete", json={"task": "grammar", "text": "hi"}, headers=HEADERS)
    assert r.status_code == 429
