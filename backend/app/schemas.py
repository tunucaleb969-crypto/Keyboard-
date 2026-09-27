from pydantic import BaseModel, Field


class CompleteRequest(BaseModel):
    task: str = Field(..., examples=["grammar", "cv", "business", "explain", "livecheck"])
    text: str = Field(..., min_length=1, max_length=4000)


class CompleteResponse(BaseModel):
    result: str
    provider: str
    model: str


class SuggestRequest(BaseModel):
    task: str = Field(..., examples=["reply", "decline", "shorten", "expand", "translate", "professional"])
    text: str = Field(..., min_length=1, max_length=4000)


class SuggestResponse(BaseModel):
    results: list[str]
    provider: str
    model: str


class HealthResponse(BaseModel):
    status: str
    provider_reachable: bool


class ModelInfo(BaseModel):
    provider: str
    model: str
    capabilities: dict
