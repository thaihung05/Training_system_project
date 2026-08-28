import os
from pathlib import Path
from dataclasses import dataclass
from dotenv import load_dotenv

PROJECT_ROOT = Path(__file__).resolve().parents[2]
load_dotenv(PROJECT_ROOT / ".env")

@dataclass(frozen=True)
class Settings:

    api_key: str
    chat_model: str

    embed_model: str

    raw_dir: Path       
    store_path: Path    

    chunk_size: int
    chunk_overlap: int
    top_k: int


    api_port: int

def load_settings() -> Settings:
    api_key = os.getenv("GEMINI_API_KEY","").strip()
    if not api_key:
        raise RuntimeError(
            "Thiếu GEMINI_API_KEY.\n Hãy sao chép .env.example thành .env và thêm GEMINI_API_KEY của bạn vào đó."
        )

    data_dir = PROJECT_ROOT / "data"
    return Settings(
        api_key=api_key,
        chat_model=os.getenv("GEMINI_CHAT_MODEL", "gemini-3.5-flash"),
        embed_model=os.getenv("EMBED_MODEL", "intfloat/multilingual-e5-small"),
        raw_dir=data_dir / "raw",
        store_path=data_dir / "processed" / "vector_store.npz",
        chunk_size=int(os.getenv("RAG_CHUNK_SIZE", 800)),
        chunk_overlap=int(os.getenv("RAG_CHUNK_OVERLAP", 120)),
        top_k=int(os.getenv("RAG_TOP_K", 5)),
        api_port=int(os.getenv("RAG_API_PORT", 8000))
    )