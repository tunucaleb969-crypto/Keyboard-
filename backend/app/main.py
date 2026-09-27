import logging

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app.config import settings
from app.providers.gemini import GeminiProvider
from app.routers.keyboard import router as keyboard_router

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ai_gateway")

app_state: dict = {}

app = FastAPI(title="AI Keyboard Gateway", version="0.1.0")


@app.on_event("startup")
async def startup() -> None:
    problems = settings.validate()
    for problem in problems:
        log.warning("Config problem: %s", problem)
    app_state["provider"] = GeminiProvider(api_key=settings.gemini_api_key, model=settings.gemini_model)


@app.on_event("shutdown")
async def shutdown() -> None:
    provider = app_state.get("provider")
    if provider is not None:
        await provider.aclose()


@app.exception_handler(Exception)
async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    # Spec requirement: the keyboard must never crash because an AI service
    # fails. Any exception that isn't already an HTTPException becomes a
    # clean 500 instead of leaking a stack trace or dropping the connection.
    log.exception("Unhandled error on %s %s", request.method, request.url.path)
    return JSONResponse(status_code=500, content={"detail": "Internal server error"})


app.include_router(keyboard_router)
