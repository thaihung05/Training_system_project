import sys
from pathlib import Path

try:
    sys.stdout.reconfigure(encoding="utf-8")
except (AttributeError, ValueError):
    pass

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from rag.pipeline import RAGPipeline


def ask_once(pipeline: RAGPipeline, question: str) -> None:
    result = pipeline.answer(question)

    print("\nTrả lời:")
    print(result.answer)

    print("\nNguồn tham khảo:")
    for i, s in enumerate(result.sources, start=1):
        print(f"  [{i}] {s.source} (điểm tương đồng: {s.score:.3f})")


def main() -> None:
    pipeline = RAGPipeline()

    if len(sys.argv) > 1:
        ask_once(pipeline, " ".join(sys.argv[1:]))
        return

    print("Chế độ hỏi-đáp RAG (gõ 'exit' để thoát)")
    while True:
        try:
            question = input("\nCâu hỏi: ").strip()
        except (EOFError, KeyboardInterrupt):
            print()
            break

        if question.lower() in {"exit", "quit", "thoat", "q"}:
            break
        if question:
            ask_once(pipeline, question)


if __name__ == "__main__":
    main()