from __future__ import annotations
from dataclasses import dataclass
from .loader import Document


@dataclass
class Chunk:
    source: str
    index: int
    text: str

def chunk_text(text: str, chunk_size: int, overlap: int) -> list[str]:
    text = text.strip()
    if not text:
        return []
    if overlap >= chunk_size:
        raise ValueError("overlap phải nhỏ hơn chunk_size")

    chunks: list[str] = []
    start = 0
    length = len(text)

    while start < length:
        end = min(start + chunk_size, length)
        chunks.append(text[start:end])
        if end == length:
            break
        start = end - overlap

    return chunks


def chunk_documents(
    documents: list[Document], chunk_size: int, overlap: int
) -> list[Chunk]:
    all_chunks: list[Chunk] = []
    for doc in documents:
        for i, piece in enumerate(chunk_text(doc.text, chunk_size, overlap)):
            all_chunks.append(Chunk(source=doc.source, index=i, text=piece))
    return all_chunks