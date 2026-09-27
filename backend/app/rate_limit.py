"""
Minimal fixed-window rate limiter, in-memory.

LIMITATION (documented): this state lives in one process's memory. It works
correctly for a single backend instance. If you scale to multiple instances
behind a load balancer, each instance counts independently — a client could
get roughly (instances x limit) requests per window. Fine for Milestone 1;
swap for Redis-backed limiting (e.g. via slowapi + redis) before scaling out.
"""
import time
from collections import defaultdict

from fastapi import HTTPException, Request, status

from app.config import settings

_window_seconds = 60
_hits: dict[str, list[float]] = defaultdict(list)


async def rate_limit(request: Request) -> None:
    client_key = request.headers.get("x-app-key", "") or (request.client.host if request.client else "unknown")
    now = time.time()
    window_start = now - _window_seconds
    hits = _hits[client_key]
    while hits and hits[0] < window_start:
        hits.pop(0)
    if len(hits) >= settings.rate_limit_per_minute:
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail="Rate limit exceeded. Try again shortly.",
        )
    hits.append(now)
