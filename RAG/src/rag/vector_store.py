from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path

import numpy as np


@dataclass
class SearchResult:
    text: str
    source: str
    score: float


class VectorStore:
    def __init__(self, vectors: np.ndarray, metadatas: list[dict]):
        self._vectors = vectors
        self._metadatas = metadatas

    @staticmethod
    def _normalize(matrix: np.ndarray) -> np.ndarray:
        norms = np.linalg.norm(matrix, axis=1, keepdims=True)
        norms[norms == 0] = 1e-12
        return matrix / norms

    @classmethod
    def build(cls, vectors: np.ndarray, metadatas: list[dict]) -> "VectorStore":
        return cls(cls._normalize(vectors.astype(np.float32)), metadatas)

    def search(self, query_vector: np.ndarray, top_k: int) -> list[SearchResult]:
        if not self._metadatas:
            return []

        q = query_vector.astype(np.float32)
        q = q / (np.linalg.norm(q) + 1e-12)

        scores = self._vectors @ q
        top_idx = np.argsort(-scores)[:top_k]

        results: list[SearchResult] = []
        for i in top_idx:
            meta = self._metadatas[int(i)]
            results.append(
                SearchResult(
                    text=meta["text"],
                    source=meta["source"],
                    score=float(scores[i]),
                )
            )
        return results

    def save(self, path: Path) -> None:
        path.parent.mkdir(parents=True, exist_ok=True)
        np.savez(path, vectors=self._vectors)
        meta_path = path.with_suffix(".meta.json")
        meta_path.write_text(
            json.dumps(self._metadatas, ensure_ascii=False), encoding="utf-8"
        )

    @classmethod
    def load(cls, path: Path) -> "VectorStore":
        if not path.exists():
            raise FileNotFoundError(
                f"Chưa có vector store tại {path}.\n"
                "-> Hãy chạy trước: python scripts/ingest.py"
            )
        data = np.load(path)
        meta_path = path.with_suffix(".meta.json")
        metadatas = json.loads(meta_path.read_text(encoding="utf-8"))
        return cls(data["vectors"], metadatas)