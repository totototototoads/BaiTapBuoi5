# Ứng dụng Quản lý Khóa học (Course Management App)

Ứng dụng di động Android (Java Native) quản lý khóa học trực tuyến kết nối REST API MockAPI.io, phục vụ bài tập lập trình Mobile App.

---

## 🌟 Chức năng nổi bật

1. **Danh sách khóa học (Course List)**:
   - Hiển thị danh sách khóa học từ MockAPI.io bằng `RecyclerView`.
   - Lấy tên danh mục động từ `GET /categories` qua liên kết `categoryId` = `categories.id` (không hardcode).
   - Tải và hiển thị ảnh bìa từ URL bằng thư viện **Glide**.
   - Định dạng học phí chuẩn VNĐ (ví dụ: `500.000 VNĐ`).
   - **Tìm kiếm**: Tìm kiếm real-time theo tên khóa học hoặc giảng viên.
   - **Lọc theo danh mục**: Chọn danh mục từ Spinner để lọc danh sách.
   - Vuốt để làm mới (`SwipeRefreshLayout`), xử lý trạng thái Loading, Lỗi mạng và Danh sách rỗng.

2. **Chi tiết khóa học (Course Detail)**:
   - Hiển thị đầy đủ thông tin: Ảnh bìa lớn, Tên, Giảng viên, Badge Danh mục, Học phí, Thời lượng, Ngày tạo và Mô tả chi tiết.
   - **Phát Video Trailer**: Nút "Xem Trailer" hỗ trợ phát cả link MP4 trực tiếp (dùng `VideoView` + bộ điều khiển `MediaController`) và link nhúng YouTube/Web (dùng `WebView`).
   - **Thao tác**: Nút Sửa khóa học và Nút Xóa khóa học (kèm AlertDialog xác nhận trước khi xóa).

3. **Thêm & Sửa khóa học (Add & Edit Course)**:
   - Form nhập liệu với kiểm tra ràng buộc (Validation):
     - Trường bắt buộc không được bỏ trống.
     - Học phí không được là số âm (`price >= 0`).
     - Thời lượng không được là số âm (`duration >= 0`).
     - Chọn danh mục từ Spinner lấy động qua API `GET /categories`.
   - Xem trước ảnh bìa (Live Image Preview) ngay khi nhập URL ảnh.

4. **Quản lý danh mục (Category Management)**:
   - Màn hình riêng quản lý danh mục khóa học (Hiển thị, Thêm, Sửa, Xóa).
   - **Ràng buộc khóa ngoại phía App**: Không cho phép xóa danh mục đang chứa khóa học liên kết (`courses.categoryId == categories.id`). Hiển thị thông báo cảnh báo rõ ràng.

5. **Cấu hình MockAPI URL linh hoạt**:
   - Tích hợp sẵn Base URL mặc định hoạt động ngay.
   - Hỗ trợ đổi Base URL MockAPI trực tiếp trong ứng dụng qua Menu Toolbar (Cài đặt) để người chấm dễ dàng nhập API của riêng mình mà không cần biên dịch lại code.

---

## 🛠️ Công nghệ & Thư viện sử dụng

- **Ngôn ngữ**: Java (Android SDK 37, minSdk 24)
- **Kiến trúc & UI**: Material Design 3, ViewBinding, ConstraintLayout, CoordinatorLayout, RecyclerView, SwipeRefreshLayout
- **Networking & REST API**: Retrofit 2 (`2.11.0`), Gson Converter, OkHttp Logging Interceptor (`4.12.0`)
- **Tải ảnh**: Glide (`4.16.0`)
- **Trình phát Video**: VideoView + MediaController & WebView HTML5 Engine

---

## 🚀 Hướng dẫn chạy ứng dụng

### 1. Chuẩn bị
- Android Studio Ladybug (2024.2.1) hoặc mới hơn.
- Thiết bị ảo Android Emulator (Android 7.0 / API 24 trở lên) hoặc máy thật có bật USB Debugging.

### 2. Các bước mở và chạy project
1. Mở Android Studio -> Chọn **Open** -> Trỏ tới thư mục `CourseManagement`.
2. Đợi Gradle Sync hoàn tất các thư viện.
3. Chọn thiết bị chạy (Emulator hoặc máy thật) và nhấn **Run 'app'** (phím tắt `Shift + F10`).

---

## 📡 REST API & Cấu hình MockAPI.io

Base URL mặc định tích hợp sẵn trong ứng dụng:
```text
https://67abbf729e85da2f020c9f11.mockapi.io/api/v1/
```

### Danh sách Endpoints
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/categories` | Lấy danh sách danh mục |
| `POST` | `/categories` | Thêm danh mục mới |
| `PUT` | `/categories/{id}` | Cập nhật danh mục |
| `DELETE` | `/categories/{id}` | Xóa danh mục |
| `GET` | `/courses` | Lấy danh sách khóa học |
| `GET` | `/courses/{id}` | Lấy chi tiết khóa học |
| `POST` | `/courses` | Thêm khóa học mới |
| `PUT` | `/courses/{id}` | Cập nhật khóa học |
| `DELETE` | `/courses/{id}` | Xóa khóa học |

---

## 📄 Dữ liệu mẫu JSON cho MockAPI

Nếu bạn tự tạo project trên https://mockapi.io, hãy tạo 2 resource là `categories` và `courses` với dữ liệu mẫu sau:

### 1. Resource: `categories`
```json
[
  {
    "id": "1",
    "name": "Lập trình Mobile",
    "description": "Android, Flutter, React Native"
  },
  {
    "id": "2",
    "name": "Lập trình Web",
    "description": "Frontend và Backend"
  },
  {
    "id": "3",
    "name": "Trí tuệ nhân tạo",
    "description": "AI, Machine Learning, Deep Learning"
  },
  {
    "id": "4",
    "name": "Khoa học Dữ liệu",
    "description": "Data Science & Big Data Analysis"
  }
]
```

### 2. Resource: `courses`
```json
[
  {
    "id": "1",
    "title": "Lập trình Android cơ bản",
    "description": "Xây dựng ứng dụng Android native chuyên nghiệp với Java và Kotlin.",
    "categoryId": "1",
    "instructor": "Nguyễn Văn A",
    "price": 500000,
    "duration": 30,
    "thumbnail": "https://picsum.photos/id/1/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    "createdAt": "2026-10-13"
  },
  {
    "id": "2",
    "title": "Lập trình Flutter toàn tập",
    "description": "Phát triển ứng dụng đa nền tảng iOS & Android với Dart và Flutter.",
    "categoryId": "1",
    "instructor": "Trần Thị B",
    "price": 750000,
    "duration": 45,
    "thumbnail": "https://picsum.photos/id/10/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "createdAt": "2026-10-12"
  },
  {
    "id": "3",
    "title": "React Native Masterclass",
    "description": "Tạo ứng dụng di động hiệu năng cao sử dụng JavaScript và React Native.",
    "categoryId": "1",
    "instructor": "Lê Hoàng C",
    "price": 680000,
    "duration": 40,
    "thumbnail": "https://picsum.photos/id/20/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
    "createdAt": "2026-10-11"
  },
  {
    "id": "4",
    "title": "Lập trình Web Frontend với ReactJS",
    "description": "Thành thạo ReactJS, Redux Toolkit và TailwindCSS cho dự án Web thực tế.",
    "categoryId": "2",
    "instructor": "Phạm Quốc D",
    "price": 600000,
    "duration": 35,
    "thumbnail": "https://picsum.photos/id/30/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
    "createdAt": "2026-10-10"
  },
  {
    "id": "5",
    "title": "Lập trình Backend với Node.js & Express",
    "description": "Xây dựng RESTful API bảo mật và mở rộng với Node.js, Express và MongoDB.",
    "categoryId": "2",
    "instructor": "Đỗ Minh E",
    "price": 800000,
    "duration": 50,
    "thumbnail": "https://picsum.photos/id/40/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
    "createdAt": "2026-10-09"
  },
  {
    "id": "6",
    "title": "Nhập môn Machine Learning",
    "description": "Học thuật toán học máy cơ bản với Python, Scikit-Learn và Pandas.",
    "categoryId": "3",
    "instructor": "Ngô Bích F",
    "price": 950000,
    "duration": 60,
    "thumbnail": "https://picsum.photos/id/50/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
    "createdAt": "2026-10-08"
  },
  {
    "id": "7",
    "title": "Deep Learning & Neural Networks",
    "description": "Xây dựng mạng nơ-ron nhân tạo với PyTorch và TensorFlow.",
    "categoryId": "3",
    "instructor": "Vũ Anh G",
    "price": 1200000,
    "duration": 70,
    "thumbnail": "https://picsum.photos/id/60/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
    "createdAt": "2026-10-07"
  },
  {
    "id": "8",
    "title": "Phân tích dữ liệu với Python & SQL",
    "description": "Kỹ năng phân tích, trực quan hóa dữ liệu kinh doanh với SQL, PowerBI và Python.",
    "categoryId": "4",
    "instructor": "Bùi Khánh H",
    "price": 550000,
    "duration": 32,
    "thumbnail": "https://picsum.photos/id/180/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
    "createdAt": "2026-10-06"
  },
  {
    "id": "9",
    "title": "Lập trình iOS với Swift & SwiftUI",
    "description": "Thiết kế giao diện hiện đại và phát triển ứng dụng iPhone chuyên nghiệp.",
    "categoryId": "1",
    "instructor": "Đặng Tuấn I",
    "price": 890000,
    "duration": 48,
    "thumbnail": "https://picsum.photos/id/160/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackBranding.mp4",
    "createdAt": "2026-10-05"
  },
  {
    "id": "10",
    "title": "Fullstack Web Development",
    "description": "Khóa học toàn diện từ Frontend HTML/CSS/JS đến Backend Node.js và DevOps cơ bản.",
    "categoryId": "2",
    "instructor": "Hoàng Nam K",
    "price": 1500000,
    "duration": 90,
    "thumbnail": "https://picsum.photos/id/201/400/300",
    "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
    "createdAt": "2026-10-04"
  }
]
```
