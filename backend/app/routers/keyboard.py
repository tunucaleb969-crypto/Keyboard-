import logging

from fastapi import APIRouter, Depends, HTTPException, status

from app.auth import verify_app_key
from app.prompts import build_multi_prompt, build_single_prompt, parse_multi_result
from app.providers.base import AIProvider, ProviderError
from app.rate_limit import rate_limit
from app.schemas import (
    CompleteRequest,
    CompleteResponse,
    HealthResponse,
    ModelInfo,
    SuggestRequest,
    SuggestResponse,
)

log = logging.getLogger("keyboard_api")

router = APIRouter(prefix="/api/v1", dependencies=[Depends(verify_app_key), Depends(rate_limit)])


def get_provider() -> AIProvider:
    # Resolve through the provider interface so route handlers stay provider-agnostic.
    from app.main import app_state
    return app_state["provider"]


@router.post("/complete", response_model=CompleteResponse)
async def complete(body: CompleteRequest):
    provider = get_provider()
    prompt = build_single_prompt(body.task, body.text)
    try:
        result = await provider.generate(prompt)
    except ProviderError as exc:
        log.warning("Provider error on /complete task=%s: %s", body.task, exc)
        raise HTTPException(status_code=status.HTTP_502_BAD_GATEWAY, detail=str(exc)) from exc
    info = provider.get_model_info()
    return CompleteResponse(result=result, provider=info["provider"], model=info["model"])


@router.post("/suggest", response_model=SuggestResponse)
async def suggest(body: SuggestRequest):
    provider = get_provider()
    prompt = build_multi_prompt(body.task, body.text)
    try:
        raw = await provider.generate(prompt)
    except ProviderError as exc:
        log.warning("Provider error on /suggest task=%s: %s", body.task, exc)
        raise HTTPException(status_code=status.HTTP_502_BAD_GATEWAY, detail=str(exc)) from exc
    info = provider.get_model_info()
    return SuggestResponse(results=parse_multi_result(raw), provider=info["provider"], model=info["model"])


@router.get("/models", response_model=list[ModelInfo])
async def list_models():
    provider = get_provider()
    info = provider.get_model_info()
    return [ModelInfo(provider=info["provider"], model=info["model"], capabilities=provider.get_capabilities())]


@router.get("/health", response_model=HealthResponse)
async def health():
    provider = get_provider()
    reachable = await provider.health_check()
    return HealthResponse(status="ok", provider_reachable=reachable)
