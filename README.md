# POS-BanHang — Hệ thống Quản lý Bán Hàng

Ứng dụng web quản lý bán hàng (POS) cho cửa hàng tạp hóa nhỏ, xây dựng bằng **Spring Boot 3 + Thymeleaf + MySQL**. Hỗ trợ bán hàng tại quầy, quản lý sản phẩm, kho, khách hàng và hóa đơn với phân quyền theo vai trò.

---

## 🧱 Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Java 17 |
| Framework | Spring Boot 3.2.3 (Web, Data JPA, Security, Validation) |
| Giao diện | Thymeleaf + Bootstrap 5 + jQuery |
| Cơ sở dữ liệu | MySQL 8 |
| Build tool | Maven (kèm Maven Wrapper `mvnw`) |

---

## ✨ Tính năng chính

- **Bán hàng (POS):** giỏ hàng theo phiên, thanh toán, tự động tính tiền thừa và trừ tồn kho trong một giao dịch (`@Transactional`).
- **Quản lý sản phẩm:** thêm / sửa / xóa, tìm kiếm & lọc theo giá ngay trên trình duyệt.
- **Quản lý kho:** tạo phiếu nhập kho, xem lịch sử nhập theo từng sản phẩm, cảnh báo tồn kho thấp.
- **Quản lý khách hàng:** thêm nhanh khi thanh toán, tìm theo số điện thoại.
- **Hóa đơn:** danh sách và chi tiết hóa đơn.
- **Phân quyền:** `ADMIN` và `STAFF` (xem bảng bên dưới), mật khẩu mã hóa BCrypt, bảo vệ CSRF.

---

## 👥 Phân quyền

| Chức năng | Khách (chưa đăng nhập) | STAFF | ADMIN |
|-----------|:----------------------:|:-----:|:-----:|
| Xem sản phẩm, thêm vào giỏ, xem giỏ | ✅ | ✅ | ✅ |
| Hóa đơn & khách hàng (`/order`, `/customers`) | ❌ | ✅ | ✅ |
| Thêm/sửa/xóa sản phẩm, quản lý kho (`/stock`) | ❌ | ❌ | ✅ |

---

## 🚀 Cài đặt & chạy

### 1. Yêu cầu
- JDK 17+
- MySQL 8 đang chạy
- (Không cần cài Maven — dùng `mvnw` kèm theo dự án)

### 2. Tạo cơ sở dữ liệu
Import file [`truyvan.sql`](truyvan.sql) — script sẽ tạo database `banhang`, toàn bộ bảng và dữ liệu mẫu:

```bash
mysql -u root -p < truyvan.sql
```

### 3. Cấu hình kết nối
Ứng dụng đọc thông tin DB từ biến môi trường, có sẵn giá trị mặc định cho môi trường dev (`localhost`, user `root`, mật khẩu rỗng).

Có **2 cách** ghi đè khi cần:

**Cách A — biến môi trường:**
```bash
# Windows PowerShell
$env:DB_URL="jdbc:mysql://localhost:3306/banhang"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="matkhau_cua_ban"
```

**Cách B — file cấu hình cục bộ (không bị commit):**
Sao chép [`application-local.properties.example`](src/main/resources/application-local.properties.example)
thành `application-local.properties`, điền thông tin thật, rồi chạy với profile `local`:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
> File `application-local.properties` đã được `.gitignore` bỏ qua nên mật khẩu của bạn sẽ không bị đẩy lên git.

### 4. Chạy ứng dụng
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```
Mở trình duyệt: **http://localhost:8080**

---

## 🔑 Tài khoản mẫu

Tất cả tài khoản mẫu dùng chung mật khẩu: **`123456`**

| Tài khoản | Vai trò | Họ tên |
|-----------|---------|--------|
| `admin` | ADMIN | Nguyễn Văn Admin |
| `staff1` | STAFF | Trần Văn Staff |
| `staff2` | STAFF | Lê Văn Staff |

---

## 🧪 Chạy kiểm thử

```bash
./mvnw test
```
> Lưu ý: test `BanhangApplicationTests` (`@SpringBootTest`) cần kết nối được MySQL.
> Các test nghiệp vụ thuần (`OrderServiceImplTest`, `ProductServiceImplTest`) chạy bằng Mockito, không cần DB.

---

## 📁 Cấu trúc thư mục

```
src/main/java/ntu/vinh/banhang/
├── config/        # Cấu hình Spring Security
├── controller/    # Controller MVC (sản phẩm, giỏ hàng, hóa đơn, kho, khách hàng)
├── entity/        # Entity JPA (Product, Invoice, Customer, User, Role...)
├── exception/     # Exception nghiệp vụ + GlobalExceptionHandler
├── model/         # Model phụ trợ (CartItem)
├── repository/    # Spring Data JPA repository
├── security/      # CustomUserDetails
└── service/       # Interface service + impl
src/main/resources/
├── templates/     # Giao diện Thymeleaf
├── static/css/    # CSS
└── application.properties
truyvan.sql        # Script tạo DB + dữ liệu mẫu
```

---

## 📝 Ghi chú

- Giỏ hàng lưu theo **session** nên không cần đăng nhập để mua, nhưng phải đăng nhập (STAFF/ADMIN) để thanh toán tạo hóa đơn.
- `spring.jpa.hibernate.ddl-auto=update` để tiện phát triển; với môi trường thật nên chuyển sang `validate` và quản lý schema bằng `truyvan.sql` / công cụ migration.
