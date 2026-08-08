from __future__ import annotations

from .vector_store import SearchResult

SYSTEM_PROMPT = (
    "Bạn là trợ lý AI trả lời câu hỏi CHỈ dựa trên phần NGỮ CẢNH được cung cấp. "
    "Nếu ngữ cảnh không chứa đủ thông tin, hãy nói rõ là bạn không tìm thấy thông tin "
    "trong tài liệu, tuyệt đối không bịa. Trả lời ngắn gọn, chính xác, bằng tiếng Việt. "
    "Khi có thể, hãy trích dẫn nguồn theo dạng [nguồn: tên_file]."
)

RAG_PROMPT_TEMPLATE = """NGỮ CẢNH:
{context}

CÂU HỎI: {question}

Hãy trả lời câu hỏi dựa trên NGỮ CẢNH ở trên."""


def build_context(results: list[SearchResult]) -> str:
    blocks = []
    for i, r in enumerate(results, start=1):
        blocks.append(f"[{i}] (nguồn: {r.source})\n{r.text}")
    return "\n\n".join(blocks)