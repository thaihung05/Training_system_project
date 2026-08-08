from __future__ import annotations

import numpy as np
from sentence_transformers import SentenceTransformer


class LocalEmbedder:
    def __init__(self, model_name: str):
        self._model = SentenceTransformer(model_name)
        self._is_e5 = "e5" in model_name.lower()

    def _encode(self, texts: list[str]) -> np.ndarray:
        vectors = self._model.encode(
            texts,
            convert_to_numpy=True,
            normalize_embeddings=True,
        )
        return vectors.astype(np.float32)

    def embed_documents(self, texts: list[str]) -> np.ndarray:
        if not texts:
            return np.zeros((0, 0), dtype=np.float32)
        prepared = [f"passage: {t}" for t in texts] if self._is_e5 else texts
        return self._encode(prepared)

    def embed_query(self, text: str) -> np.ndarray:
        prepared = f"query: {text}" if self._is_e5 else text
        return self._encode([prepared])[0]