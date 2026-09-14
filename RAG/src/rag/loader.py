from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from pypdf import PdfReader

_TEXT_SUFFIXES = {".txt", ".md", ".markdown"}


@dataclass
class Document:
    source: str
    text: str


def _read_pdf(path: Path) -> str:
    reader = PdfReader(str(path))
    return "\n".join((page.extract_text() or "") for page in reader.pages)


def load_documents(raw_dir: Path) -> list[Document]:
    documents: list[Document] = []
    if not raw_dir.exists():
        return documents

    for path in sorted(raw_dir.rglob("*")):
        if not path.is_file():
            continue

        suffix = path.suffix.lower()
        if suffix in _TEXT_SUFFIXES:
            text = path.read_text(encoding="utf-8")
        elif suffix == ".pdf":
            text = _read_pdf(path)
        else:
            continue

        text = text.strip()
        if text:
            documents.append(Document(source=str(path.relative_to(raw_dir)), text=text))

    return documents