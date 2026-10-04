"""Generic OpenAI-compatible chat-completions provider.

Use this adapter for any trusted service exposing the OpenAI-compatible API.
Credentials and endpoint configuration remain server-side. Availability/free-tier
terms depend on the selected host and are not assumed by this code.
"""
import httpx

from app.providers.base import AIProvider, ProviderError


class OpenAICompatibleProvider(AIProvider):
    def __init__(self, api_key: str, base_url: str, model: str):
        self._api_key = api_key
        self._base_url = base_url.rstrip("/")
        self._model = model
        self._client = httpx.AsyncClient(timeout=httpx.Timeout(25.0, connect=10.0))

    async def generate(self, prompt: str, max_output_tokens: int = 500) -> str:
        if not self._api_key or not self._base_url or not self._model:
            raise ProviderError("OpenAI-compatible provider is not fully configured")
        try:
            response = await self._client.post(
                f"{self._base_url}/chat/completions",
                headers={"Authorization": f"Bearer {self._api_key}"},
                json={
                    "model": self._model,
                    "messages": [{"role": "user", "content": prompt}],
                    "max_tokens": max_output_tokens,
                },
            )
        except httpx.RequestError as exc:
            raise ProviderError(f"OpenAI-compatible provider network error: {exc}") from exc
        if response.status_code != 200:
            # Avoid logging or returning request headers/API keys.
            raise ProviderError(f"OpenAI-compatible API error {response.status_code}")
        try:
            content = response.json()["choices"][0]["message"]["content"]
            if not isinstance(content, str) or not content.strip():
                raise ValueError("empty message content")
            return content.strip()
        except (KeyError, IndexError, TypeError, ValueError) as exc:
            raise ProviderError("OpenAI-compatible provider returned a malformed response") from exc

    async def health_check(self) -> bool:
        if not self._api_key or not self._base_url or not self._model:
            return False
        try:
            await self.generate("Reply with the single word: ok", max_output_tokens=5)
            return True
        except ProviderError:
            return False

    def get_capabilities(self) -> dict:
        return {"streaming": False, "multi_result": True, "tasks": [
            "grammar", "explain", "cv", "business", "livecheck",
            "reply", "decline", "shorten", "expand", "translate", "tone",
        ]}

    def get_model_info(self) -> dict:
        return {"provider": "openai-compatible", "model": self._model}

    async def aclose(self):
        await self._client.aclose()
