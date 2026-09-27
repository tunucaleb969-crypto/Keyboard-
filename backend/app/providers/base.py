"""
Common provider abstraction. Every AI provider (Gemini today; Claude, OpenAI,
or a future self-trained model later) implements this same interface so the
router and API layer never need provider-specific logic.
"""
from abc import ABC, abstractmethod
from typing import Any


class ProviderError(Exception):
    """Raised when a provider call fails (network, auth, malformed response)."""


class AIProvider(ABC):
    @abstractmethod
    async def generate(self, prompt: str, max_output_tokens: int = 500) -> str:
        """Send a single prompt, return the model's text response."""
        raise NotImplementedError

    @abstractmethod
    async def health_check(self) -> bool:
        """Lightweight check that the provider is reachable/configured."""
        raise NotImplementedError

    @abstractmethod
    def get_capabilities(self) -> dict[str, Any]:
        """Static description of what this provider supports."""
        raise NotImplementedError

    @abstractmethod
    def get_model_info(self) -> dict[str, Any]:
        """Provider name, model id, and other registry metadata."""
        raise NotImplementedError
