# Hệ thống đào tạo nhân viên nội bộ kết hợp với RAG Chatbot hỗ trợ

Đồ án tốt nghiệp ngành Công nghệ thông tin. Hệ thống đào tạo nhân viên nội bộ (LMS) cho doanh nghiệp bán lẻ có cơ cấu Chuỗi - Vùng - Siêu thị, tích hợp trợ lý ảo RAG hỗ trợ trả lời câu hỏi dựa trên tài liệu đào tạo.

## Công nghệ sử dụng

- **Backend**: Java, Spring MVC, Hibernate, MySQL
- **Frontend**: Vue 3, Pinia, Vue Router, Vite
- **Trợ lý ảo RAG**: Python, FastAPI, Gemini API

## Cấu trúc thư mục

```
TrainingSystem/           backend (Spring MVC)
TrainingSystemFrontend/   frontend (Vue 3)
RAG/                       microservice trợ lý ảo RAG
Data import mẫu/          file Excel mẫu để import người dùng, câu hỏi
database.sql              dữ liệu mẫu để import vào MySQL
Testcase.xlsx             ma trận test case
```

## Cách chạy hệ thống

### 1. Database

- Tạo database MySQL tên `training_system`, import file `database.sql`.
- Cập nhật lại thông tin kết nối (user, password) trong `TrainingSystem/src/main/resources/databases.properties`.

### 2. Backend

- Mở thư mục `TrainingSystem` bằng NetBeans, chạy trên Tomcat 11.
- Sau khi chạy, backend phục vụ tại `http://localhost:8080/TrainingSystem`.

### 3. Frontend

```
cd TrainingSystemFrontend
npm install
npm run dev
```

- Cần file `.env` trong `TrainingSystemFrontend` với nội dung:
```
VITE_API_BASE_URL=http://localhost:8080/TrainingSystem
```

### 4. Trợ lý ảo RAG

```
cd RAG
pip install -r requirements.txt
python scripts/ingest.py   # nạp tài liệu vào kho vector, chỉ cần chạy khi thêm/sửa tài liệu
python app.py               # chạy service tại http://localhost:8000
```

- Cần file `.env` trong `RAG` với nội dung tối thiểu:
```
GEMINI_API_KEY=<api key Gemini của bạn>
```

## Tài khoản demo

Xem danh sách tài khoản mẫu trong `Data import mẫu/User.xlsx`, hoặc dùng các tài khoản đã có sẵn trong `database.sql` (ví dụ `demo.admin`, `demo.trainer`, `demo.employee`).
