"""
Gemini provider.

Endpoint/body/response shape verified against ai.google.dev docs
(confirmed current as of this writing):
  POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent
  header: x-goog-api-key
  body: { contents: [ { parts: [ { text } ] } ], generationConfig }
  response: candidates[0].content.parts[0].text

gemini-3.1-flash-lite is the current stable GA model (verified: GA since
May 7, 2026, per ai.google.dev changelog). This matches what the existing
Android AIClient.kt already used, so behavior is preserved, not changed.
"""
import httpx

from app.providers.base import AIProvider, ProviderError

GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"


class GeminiProvider(AIProvider):
    def __init__(self, api_key: str, model: str):
        self._api_key = api_key
        self._model = model
        self._client = httpx.AsyncClient(timeout=httpx.Timeout(connect=15.0, read=20.0))

    async def generate(self, prompt: str, max_output_tokens: int = 500) -> str:
        url = f"{GEMINI_BASE_URL}/{self._model}:generateContent"
        body = {
            "contents": [{"parts": [{"text": prompt}]}],
            "generationConfig": {"maxOutputTokens": max_output_tokens},
        }
        headers = {
            "x-goog-api-key": self._api_key,
            "content-type": "application/json",
        }
        try:
            response = await self._client.post(url, json=body, headers=headers)
        except httpx.RequestError as exc:
            # Covers DNS failure, connection refused, timeout, etc.
            raise ProviderError(f"Gemini network error: {exc}") from exc

        if response.status_code != 200:
            raise ProviderError(f"Gemini API error {response.status_code}: {response.text}")

        try:
            data = response.json()
            return data["candidates"][0]["content"]["parts"][0]["text"].strip()
        except (KeyError, IndexError, ValueError) as exc:
            raise ProviderError(f"Gemini returned a malformed response: {exc}") from exc

    async def health_check(self) -> bool:
        if not self._api_key:
            return False
        try:
            await self.generate("Reply with the single word: ok", max_output_tokens=5)
            return True
        except ProviderError:
            return False

    def get_capabilities(self) -> dict:
        return {
            "streaming": False,
            "multi_result": True,
            "tasks": [
                "grammar", "explain", "cv", "business", "livecheck",
                "reply", "decline", "shorten", "expand", "translate", "tone",
            ],
        }

    def get_model_info(self) -> dict:
        return {"provider": "gemini", "model": self._model}

    async def aclose(self):
        await self._client.aclose()
