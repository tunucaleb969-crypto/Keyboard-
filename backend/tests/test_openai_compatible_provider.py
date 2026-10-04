import asyncio

import pytest

from app.providers.base import ProviderError
from app.providers.openai_compatible import OpenAICompatibleProvider


class FakeResponse:
    def __init__(self, status_code=200, payload=None):
        self.status_code = status_code
        self._payload = payload or {"choices": [{"message": {"content": "  hello there  "}}]}

    def json(self):
        return self._payload


class FakeClient:
    def __init__(self, response=None, error=None):
        self.response = response or FakeResponse()
        self.error = error
        self.last_request = None

    async def post(self, url, headers, json):
        self.last_request = (url, headers, json)
        if self.error:
            raise self.error
        return self.response

    async def aclose(self):
        pass


def test_openai_compatible_provider_parses_response_and_keeps_auth_server_side():
    provider = OpenAICompatibleProvider("server-secret", "https://provider.example/v1/", "model-x")
    fake = FakeClient()
    provider._client = fake
    result = asyncio.run(provider.generate("test prompt", max_output_tokens=42))
    assert result == "hello there"
    url, headers, body = fake.last_request
    assert url == "https://provider.example/v1/chat/completions"
    assert headers["Authorization"] == "Bearer server-secret"
    assert body["model"] == "model-x"
    assert body["max_tokens"] == 42


def test_openai_compatible_provider_rejects_missing_configuration_without_network():
    provider = OpenAICompatibleProvider("", "", "")
    with pytest.raises(ProviderError):
        asyncio.run(provider.generate("test"))


def test_openai_compatible_provider_rejects_malformed_response():
    provider = OpenAICompatibleProvider("server-secret", "https://provider.example/v1", "model-x")
    provider._client = FakeClient(response=FakeResponse(payload={"choices": []}))
    with pytest.raises(ProviderError, match="malformed response"):
        asyncio.run(provider.generate("test"))
