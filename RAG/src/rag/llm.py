from __future__ import annotations

from google import genai
from google.genai import types


class GeminiLLM:
    def __init__(self, client: genai.Client, model: str):
        self._client = client
        self._model = model

    def generate(self, prompt: str, system: str | None = None) -> str:
        config = None
        if system:
            config = types.GenerateContentConfig(system_instruction=system)

        response = self._client.models.generate_content(
            model=self._model,
            contents=prompt,
            config=config,
        )
        return (response.text or "").strip()