from __future__ import annotations
from dataclasses import dataclass
from google import genai
from google.genai import types
from . import prompts
from .chunker import chunk_documents
from .config import Settings, load_settings
from .embeddings import LocalEmbedder
from .llm import GeminiLLM
from .loader import load_documents
from .vector_store import SearchResult, VectorStore


@dataclass
class RAGAnswer:
    answer: str
    sources: list[SearchResult]

class RAGPipeline:
    def __init__(self, settings: Settings | None = None):
        self.settings = settings or load_settings()

        http_options = types.HttpOptions(
            retry_options=types.HttpRetryOptions(
                attempts=4,
                initial_delay=1.0,
                max_delay=10.0,
                http_status_codes=[429, 500, 502, 503, 504],
            )
        )

        client = genai.Client(
            api_key=self.settings.api_key, http_options=http_options
        )
        self.llm = GeminiLLM(client, self.settings.chat_model)
        self.embedder = LocalEmbedder(self.settings.embed_model)

        self._store: VectorStore | None = None

    def ingest(self) -> int:
        documents = load_documents(self.settings.raw_dir)
        if not documents:
            raise RuntimeError(
                f"Không tìm thấy tài liệu nào trong {self.settings.raw_dir}.\n"
                "=> Hãy bỏ vài file .txt/.md/.pdf vào thư mục data/raw/ rồi thử lại."
            )

        chunks = chunk_documents(
            documents, self.settings.chunk_size, self.settings.chunk_overlap
        )
        vectors = self.embedder.embed_documents([c.text for c in chunks])
        metadatas = [
            {"text": c.text, "source": c.source, "index": c.index} for c in chunks
        ]

        store = VectorStore.build(vectors, metadatas)
        store.save(self.settings.store_path)
        self._store = store
        return len(chunks)

    def answer(self, question: str) -> RAGAnswer:
        store = self._get_store()

        query_vector = self.embedder.embed_query(question)
        results = store.search(query_vector, self.settings.top_k)

        context = prompts.build_context(results)
        prompt = prompts.RAG_PROMPT_TEMPLATE.format(
            context=context, question=question
        )
        answer_text = self.llm.generate(prompt, system=prompts.SYSTEM_PROMPT)

        return RAGAnswer(answer=answer_text, sources=results)

    def _get_store(self) -> VectorStore:
        if self._store is None:
            self._store = VectorStore.load(self.settings.store_path)
        return self._store