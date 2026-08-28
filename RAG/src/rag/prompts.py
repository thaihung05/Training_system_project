from __future__ import annotations

from .vector_store import SearchResult

SYSTEM_PROMPT = (
    "Bạn là trợ lý ảo hỗ trợ nhân viên, trả lời câu hỏi CHỈ dựa trên phần NGỮ CẢNH được cung cấp. "
    "Nếu ngữ cảnh không chứa đủ thông tin để trả lời, hãy nói rõ là bạn không tìm thấy thông tin "
    "trong tài liệu, tuyệt đối không bịa thêm.\n\n"
    "Khi trả lời:\n"
    "- Viết bằng tiếng Việt, ngắn gọn, rõ ràng, dễ hiểu.\n"
    "- Xuống dòng giữa các ý; nếu có nhiều bước hoặc nhiều ý, trình bày thành gạch đầu dòng.\n"
    "- Không cần trích dẫn tên nguồn/tên file tài liệu trong câu trả lời."
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