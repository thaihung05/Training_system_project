import sys
from pathlib import Path

try:
    sys.stdout.reconfigure(encoding="utf-8")
except (AttributeError, ValueError):
    pass

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))
from rag.pipeline import RAGPipeline


def main() -> None:
    pipeline = RAGPipeline()
    print(f"Đang đọc tài liệu từ: {pipeline.settings.raw_dir}")

    num_chunks = pipeline.ingest()

    print(f"Đã tạo {num_chunks} chunk.")
    print(f"Vector store đã lưu tại: {pipeline.settings.store_path}")


if __name__ == "__main__":
    main()