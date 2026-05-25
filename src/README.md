# Hệ thống Quản lý Nhân sự

Một ứng dụng desktop quản lý nhân sự viết bằng Java Swing, kết nối cơ sở dữ liệu MySQL.

## Tính năng

- **Đăng nhập & Phân quyền** — Đăng nhập với tài khoản mật khẩu, phân quyền Admin (sửa/xóa) và User (chỉ xem).
- **Quản lý nhân viên** — Thêm, sửa, xóa nhân viên với đầy đủ thông tin.
- **Phân loại nhân viên** — Hỗ trợ 3 loại: Toàn thời gian (lương cơ bản + phụ cấp), Bán thời gian (số giờ x lương giờ), Quản lý (lương cơ bản + thưởng).
- **Phòng ban** — Phân loại nhân viên theo phòng ban, lọc danh sách theo phòng ban.
- **Tìm kiếm** — Tìm kiếm nhân viên theo mã hoặc tên.
- **Xem chi tiết** — Xem toàn bộ thông tin nhân viên ở giao diện riêng.
- **Thống kê** — Thống kê số lượng nhân viên, tổng lương, lương trung bình, phân bổ theo loại và phòng ban, kèm biểu đồ cột.
- **Xuất CSV** — Xuất danh sách nhân viên ra file CSV (hỗ trợ tiếng Việt trong Excel).
- **Phân trang** — Hiển thị 20 nhân viên mỗi trang, điều hướng Trước/Sau.
- **Đăng xuất** — Đăng xuất và quay lại màn hình đăng nhập.

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 8+ |
| Giao diện | Java Swing (Nimbus LookAndFeel) |
| Cơ sở dữ liệu | MySQL 8+ |
| Kết nối DB | JDBC (mysql-connector-j-9.6.0) |
| Icon | Java2D vector tự vẽ |

## Cài đặt

### Yêu cầu

- Java Development Kit (JDK) 8 trở lên
- MySQL 8 trở lên
- Trình soạn thảo mã nguồn (IntelliJ, VS Code, Eclipse, ...)

### Cấu hình cơ sở dữ liệu

Đường dẫn: `utils/dbConnection.java`

```java
private static final String BASE_URL = "jdbc:mysql://localhost:3306";
private static final String DB_NAME = "quanlynhansu";
private static final String USER = "root";
private static final String PASSWORD = "";
```

Sửa thông tin `USER` và `PASSWORD` cho phù hợp với máy của bạn.

### Khởi tạo cơ sở dữ liệu

**Cách 1 — Tự động:** Ứng dụng sẽ tự động chạy file `database/init.sql` để tạo database và bảng khi kết nối lần đầu nếu chưa có.

**Cách 2 — Thủ công:** Chạy lệnh sau trong terminal:

```bash
mysql -u root < database/init.sql
```

File `init.sql` bao gồm:
- Tạo database `quanlynhansu`
- Tạo bảng `Employees` và `Users`
- Dữ liệu mẫu (2 tài khoản, 5 nhân viên)

### Tài khoản mặc định

| Tài khoản | Mật khẩu | Vai trò |
|---|---|---|
| `admin` | `123` | Quản trị (Admin) |
| `user1` | `123` | Người dùng (User) |

## Biên dịch và chạy

Sử dụng Maven Wrapper ở thư mục gốc của dự án để chạy ứng dụng:

```bash
# Chạy ứng dụng trực tiếp bằng Spring Boot plugin
./mvnw spring-boot:run

# Hoặc đóng gói thành file JAR và chạy
./mvnw clean package
java -jar target/demojava-0.0.1-SNAPSHOT.jar
```

## Cấu trúc thư mục

```
demojava/
├── .mvn/             # Maven Wrapper configuration
├── mvnw/mvnw.cmd     # Maven Wrapper scripts
├── pom.xml           # Cấu hình dự án Maven & Dependencies
├── target/           # Thư mục chứa file build đầu ra (.class & .jar)
├── src/              # Mã nguồn dự án
│   ├── dao/          # Data Access Object — truy vấn cơ sở dữ liệu
│   │   ├── employeeDAO.java
│   │   └── userDAO.java
│   ├── database/     # Script khởi tạo cơ sở dữ liệu
│   │   └── init.sql
│   ├── lib/          # Thư viện JAR dự phòng (FlatLaf, MySQL Connector)
├── model/            # Lớp mô hình dữ liệu
│   ├── employee.java (abstract)
│   ├── fullTimeEmployee.java
│   ├── iManageable.java (interface)
│   ├── manager.java
│   ├── partTimeEmployee.java
│   └── user.java
├── resources/
│   └── icons/        # File SVG biểu tượng
├── utils/            # Tiện ích
│   ├── dbConnection.java
│   └── IconUtils.java
├── view/             # Giao diện Swing
│   ├── employeeDetailView.java
│   ├── employeeDialog.java
│   ├── loginView.java
│   ├── mainView.java
│   └── statisticsDialog.java
├── Main.java         # Điểm vào của ứng dụng
└── README.md
```

## Ghi chú

- Ứng dụng sử dụng phông chữ **Segoe UI** — có sẵn trên Windows, nếu dùng hệ điều hành khác nên cài đặt hoặc sửa font trong mã nguồn.
- Cơ chế phân trang hiển thị tối đa **20 nhân viên** mỗi trang.
- File CSV xuất ra có BOM (Byte Order Mark) để hiển thị đúng tiếng Việt trong Microsoft Excel.
