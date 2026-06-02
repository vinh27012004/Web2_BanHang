CREATE DATABASE IF NOT EXISTS banhang;
USE banhang;

-- Bảng role
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Bảng user
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255)
);

-- Bảng user_roles
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Bảng sản phẩm
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng khách hàng
CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100)
);

-- Bảng hóa đơn
CREATE TABLE invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DOUBLE NOT NULL,
    customer_paid DOUBLE NOT NULL,
    change_amount DOUBLE NOT NULL,
    user_id BIGINT NOT NULL,
    customer_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Bảng chi tiết hóa đơn
CREATE TABLE invoice_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Bảng nhập kho
CREATE TABLE stock_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    quantity INT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Chỉ mục tối ưu hóa hiệu năng truy vấn (Performance Indexes)
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_invoice_created_at ON invoice(created_at);
CREATE INDEX idx_stock_entry_created_at ON stock_entry(created_at);

-- Dữ liệu mẫu cho role
INSERT INTO role (id, name) VALUES
(1, 'ADMIN'),
(2, 'STAFF');

-- Dữ liệu mẫu cho user (password: 123456)
INSERT INTO user (id, username, password, full_name) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Nguyễn Văn Admin'),
(2, 'staff1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Trần Văn Staff'),
(3, 'staff2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Lê Văn Staff');

-- Dữ liệu mẫu cho user_roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin có role ADMIN
(2, 2), -- staff1 có role STAFF
(3, 2); -- staff2 có role STAFF

-- Dữ liệu mẫu cho sản phẩm
INSERT INTO product (code, name, price, quantity) VALUES
('SP001', 'Coca Cola lon', 10000, 100),
('SP002', 'Snack Oishi', 5000, 200),
('SP003', 'Nước suối Aquafina', 7000, 150),
('SP004', 'Bánh Oreo', 12000, 80),
('SP005', 'Kẹo mút Chupa Chups', 3000, 300);

-- Dữ liệu mẫu cho khách hàng
INSERT INTO customer (name, phone, email) VALUES
('Nguyễn Văn A', '0123456789', 'nguyenvana@gmail.com'),
('Trần Thị B', '0987654321', 'tranthib@gmail.com'),
('Lê Văn C', '0912345678', 'levanc@gmail.com');

-- Dữ liệu mẫu cho hóa đơn
INSERT INTO invoice (id, total_amount, customer_paid, change_amount, user_id, customer_id) VALUES
(1, 25000, 50000, 25000, 2, 1), -- Hóa đơn của staff1 cho khách hàng 1
(2, 15000, 20000, 5000, 3, 2),  -- Hóa đơn của staff2 cho khách hàng 2
(3, 30000, 50000, 20000, 2, 3); -- Hóa đơn của staff1 cho khách hàng 3

-- Dữ liệu mẫu cho chi tiết hóa đơn
INSERT INTO invoice_item (invoice_id, product_id, quantity, unit_price, total_price) VALUES
(1, 1, 2, 10000, 20000), -- 2 lon Coca Cola
(1, 2, 1, 5000, 5000),   -- 1 gói Snack
(2, 3, 2, 7000, 14000),  -- 2 chai nước suối
(2, 2, 1, 5000, 5000),   -- 1 gói Snack
(3, 4, 2, 12000, 24000), -- 2 gói bánh Oreo
(3, 5, 2, 3000, 6000);   -- 2 kẹo mút

-- Dữ liệu mẫu cho nhập kho
INSERT INTO stock_entry (product_id, quantity, note) VALUES
(1, 50, 'Nhập thêm Coca Cola'),
(2, 100, 'Nhập thêm Snack'),
(3, 75, 'Nhập thêm nước suối'),
(4, 40, 'Nhập thêm bánh Oreo'),
(5, 150, 'Nhập thêm kẹo mút');

