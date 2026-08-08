from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent / "src"))

from fastapi import FastAPI
from pydantic import BaseModel

from rag.pipeline import RAGPipeline

app = FastAPI(title="Training System RAG Service")
pipeline = RAGPipeline()


class AskRequest(BaseModel):
    question: str


class AskResponse(BaseModel):
    answer: str


@app.post("/ask", response_model=AskResponse)
def ask(body: AskRequest) -> AskResponse:
    result = pipeline.answer(body.question)
    return AskResponse(answer=result.answer)