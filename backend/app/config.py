"""
Configuration loaded from environment variables.

Required env vars (see .env.example):
  GEMINI_API_KEY   - server-side Gemini API key. NEVER shipped to the Android app.
  APP_SHARED_SECRET - shared secret the Android app sends in the
                       X-App-Key header, so this gateway only serves our app.
Optional:
  GEMINI_MODEL     - defaults to gemini-3.1-flash-lite (verified current GA
                      model on ai.google.dev as of this writing).
  RATE_LIMIT_PER_MINUTE - per-client request cap. Defaults to 30.
"""
import os


class Settings:
    gemini_api_key: str = os.environ.get("GEMINI_API_KEY", "")
    gemini_model: str = os.environ.get("GEMINI_MODEL", "gemini-3.1-flash-lite")
    app_shared_secret: str = os.environ.get("APP_SHARED_SECRET", "")
    rate_limit_per_minute: int = int(os.environ.get("RATE_LIMIT_PER_MINUTE", "30"))

    def validate(self) -> list[str]:
        """Returns a list of problems. Empty list = config OK."""
        problems = []
        if not self.gemini_api_key:
            problems.append("GEMINI_API_KEY is not set")
        if not self.app_shared_secret:
            problems.append("APP_SHARED_SECRET is not set")
        return problems


settings = Settings()
