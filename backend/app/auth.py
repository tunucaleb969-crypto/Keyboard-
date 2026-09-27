"""
App-level authentication: this is NOT the Gemini key. This is a separate
shared secret that only our Android app knows, so this gateway doesn't sit
open on the internet for anyone to call.

LIMITATION (documented, not hidden): a single shared secret authenticates
"this is our app," not "this is user X." It stops random internet abuse of
your Gemini quota, but every install shares one credential. Per-user auth
(e.g. Firebase Auth + per-user rate limits) is future work once there are
real users and abuse patterns to design against — do not treat this as
done/production-hardened auth.
"""
from fastapi import Header, HTTPException, status

from app.config import settings


async def verify_app_key(x_app_key: str = Header(default="")) -> None:
    if not settings.app_shared_secret:
        # Fails closed: if the server itself isn't configured, refuse all traffic
        # rather than silently accepting unauthenticated requests.
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Server misconfigured: APP_SHARED_SECRET not set",
        )
    if x_app_key != settings.app_shared_secret:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or missing X-App-Key")
