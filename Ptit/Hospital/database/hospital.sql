-- =====================================================
-- DROP DATABASE CŨ NẾU CÓ, TẠO LẠI TỪ ĐẦU
-- =====================================================
DROP DATABASE IF EXISTS hospital_db;
CREATE DATABASE hospital_db;
USE hospital_db;

-- =====================================================
-- BẢNG departments (khoa)
-- =====================================================
CREATE TABLE departments (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
description TEXT,
image VARCHAR(255),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- BẢNG doctors (bác sĩ)
-- =====================================================
CREATE TABLE doctors (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
department_id BIGINT NOT NULL,
qualification VARCHAR(100),
experience INT DEFAULT 0,
avatar VARCHAR(255),
description TEXT,
fee DECIMAL(12,0) DEFAULT 150000,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- =====================================================
-- BẢNG users (bệnh nhân + admin)
-- =====================================================
CREATE TABLE users (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
fullname VARCHAR(100) NOT NULL,
phone VARCHAR(15) UNIQUE NOT NULL,
email VARCHAR(100),
password VARCHAR(100) NOT NULL,
role VARCHAR(20) DEFAULT 'USER',  -- USER, ADMIN
dob DATE,
gender VARCHAR(10),
address TEXT,
identity_no VARCHAR(20),
insurance_no VARCHAR(20),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- BẢNG appointments (lịch khám)
-- =====================================================
CREATE TABLE appointments (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
appointment_code VARCHAR(20) UNIQUE NOT NULL,
patient_name VARCHAR(100) NOT NULL,
patient_phone VARCHAR(15) NOT NULL,
patient_email VARCHAR(100),
patient_dob DATE,
patient_gender VARCHAR(10),
patient_address TEXT,
identity_no VARCHAR(20),
insurance_no VARCHAR(20),
doctor_id BIGINT NOT NULL,
department_id BIGINT NOT NULL,
appointment_date DATE NOT NULL,
slot VARCHAR(20) NOT NULL,
symptoms TEXT,
status VARCHAR(20) DEFAULT 'PENDING',
user_id BIGINT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (doctor_id) REFERENCES doctors(id),
FOREIGN KEY (department_id) REFERENCES departments(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
-- BẢNG medical_records (bệnh án / kết quả khám)
-- =====================================================
CREATE TABLE medical_records (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
appointment_id BIGINT NOT NULL,
diagnosis TEXT,
prescription TEXT,
notes TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- =====================================================
-- BẢNG invoices (hóa đơn)
-- =====================================================
CREATE TABLE invoices (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
appointment_id BIGINT NOT NULL,
total_amount DECIMAL(12,0) NOT NULL,
status VARCHAR(20) DEFAULT 'UNPAID',
payment_date DATE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- =====================================================
-- BẢNG articles (tin tức, bài viết sức khỏe)
-- =====================================================
CREATE TABLE articles (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
title VARCHAR(200) NOT NULL,
summary TEXT,
content LONGTEXT,
image VARCHAR(255),
category VARCHAR(50),
views INT DEFAULT 0,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- =====================================================
-- DỮ LIỆU MẪU (đầy đủ)
-- =====================================================
-- =====================================================

-- ===== 1. KHOA (6 khoa) =====
INSERT INTO departments (name, description) VALUES
('Khoa Da liễu Tổng hợp', 'Khám và điều trị các bệnh lý về da, tóc, móng thường gặp như viêm da cơ địa, vảy nến, mề đay, zona thần kinh, lang ben, hắc lào...'),
('Khoa Da liễu Thẩm mỹ', 'Điều trị nám, tàn nhang, sẹo rỗ, sẹo lồi, xóa xăm, trẻ hóa da, điều trị mụn, tẩy lông bằng công nghệ laser và ánh sáng'),
('Khoa Phẫu thuật Tạo hình', 'Phẫu thuật sửa chữa dị tật bẩm sinh (sứt môi, hở hàm ếch), tạo hình sẹo, ghép da, cắt bỏ khối u vùng da, thẩm mỹ vùng mặt'),
('Khoa Da liễu Nhi', 'Chuyên khám và điều trị các bệnh da liễu ở trẻ em: viêm da cơ địa, rôm sảy, chàm sữa, nổi mề đay, bỏng, nhiễm trùng da'),
('Khoa Kiểm soát Nhiễm khuẩn', 'Tư vấn và điều trị các bệnh nhiễm khuẩn lây qua da như ghẻ, rận, nấm da, viêm nang lông, áp xe da, zona'),
('Khoa Nghiên cứu và Ứng dụng Công nghệ cao', 'Ứng dụng công nghệ mới trong chẩn đoán và điều trị bệnh da như laser CO2, IPL, trị liệu ánh sáng, tế bào gốc');

-- ===== 2. BÁC SĨ (12 bác sĩ, mỗi khoa 2 bác sĩ) =====
INSERT INTO doctors (name, department_id, qualification, experience, fee) VALUES
-- Khoa Da liễu Tổng hợp (id 1)
('PGS.TS Nguyễn Văn An', 1, 'Trưởng khoa, Chuyên gia đầu ngành Da liễu', 25, 350000),
('TS.BS Trần Thị Bình', 1, 'Phó trưởng khoa, Bác sĩ cao cấp', 20, 280000),

-- Khoa Da liễu Thẩm mỹ (id 2)
('BS.CKII Lê Văn Cường', 2, 'Trưởng khoa Thẩm mỹ Da liễu', 18, 400000),
('TS.BS Phạm Thị Dung', 2, 'Chuyên gia Trẻ hóa da, Cố vấn thẩm mỹ', 15, 380000),

-- Khoa Phẫu thuật Tạo hình (id 3)
('PGS.TS Hoàng Văn Em', 3, 'Trưởng khoa Phẫu thuật Tạo hình', 22, 450000),
('BS.CKI Nguyễn Thị Phương', 3, 'Bác sĩ Phẫu thuật Tạo hình', 12, 320000),

-- Khoa Da liễu Nhi (id 4)
('TS.BS Vũ Thị Giang', 4, 'Trưởng khoa Da liễu Nhi', 14, 300000),
('BS Đặng Văn Hải', 4, 'Bác sĩ Da liễu Nhi', 8, 220000),

-- Khoa Kiểm soát Nhiễm khuẩn (id 5)
('PGS.TS Lê Thị Hoa', 5, 'Trưởng khoa Kiểm soát Nhiễm khuẩn', 20, 320000),
('TS.BS Nguyễn Văn Khánh', 5, 'Bác sĩ điều trị nhiễm khuẩn da', 10, 250000),

-- Khoa Công nghệ cao (id 6)
('TS.BS Trần Quang Minh', 6, 'Trưởng khoa Ứng dụng Công nghệ cao', 12, 420000),
('BS Nguyễn Hoàng Nam', 6, 'Chuyên gia Laser và công nghệ cao', 7, 300000);

-- ===== 3. TÀI KHOẢN USERS (admin + bệnh nhân mẫu) =====
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address) VALUES
('Quản trị viên', '0988888888', 'admin@hospital.com', 'admin123', 'ADMIN', '1980-01-01', 'MALE', 'Hà Nội'),
('Nguyễn Văn Bệnh', '0912345678', 'benh@email.com', '123456', 'USER', '1990-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội'),
('Trần Thị Khỏe', '0923456789', 'khoe@email.com', '123456', 'USER', '1995-08-20', 'FEMALE', 'Số 45, Nguyễn Trãi, Hà Nội'),
('Lê Văn A', '0934567890', 'lea@email.com', '123456', 'USER', '1988-03-10', 'MALE', 'Số 78, Xã Đàn, Hà Nội'),
('Phạm Thị B', '0945678901', 'phamb@email.com', '123456', 'USER', '2000-11-25', 'FEMALE', 'Số 23, Cầu Giấy, Hà Nội');

-- ===== 4. LỊCH HẸN (15 lịch, nhiều trạng thái khác nhau) =====
INSERT INTO appointments (appointment_code, patient_name, patient_phone, patient_email, patient_dob, patient_gender, patient_address, identity_no, insurance_no, doctor_id, department_id, appointment_date, slot, symptoms, status, user_id) VALUES
('KH202604280001', 'Nguyễn Văn Bệnh', '0912345678', 'benh@email.com', '1990-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội', '00123456789', 'BHYT123456', 1, 1, '2026-05-01', 'MORNING', 'Nổi mẩn đỏ khắp người, ngứa nhiều', 'COMPLETED', 2),
('KH202604280002', 'Nguyễn Văn Bệnh', '0912345678', 'benh@email.com', '1990-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội', '00123456789', 'BHYT123456', 3, 2, '2026-05-10', 'AFTERNOON', 'Nám da vùng má, da khô sạm', 'COMPLETED', 2),
('KH202604280003', 'Trần Thị Khỏe', '0923456789', 'khoe@email.com', '1995-08-20', 'FEMALE', 'Số 45, Nguyễn Trãi, Hà Nội', '00987654321', 'BHYT654321', 1, 1, '2026-05-05', 'MORNING', 'Nổi mề đay, nổi mẩn sau khi ăn hải sản', 'CONFIRMED', 3),
('KH202604280004', 'Trần Thị Khỏe', '0923456789', 'khoe@email.com', '1995-08-20', 'FEMALE', 'Số 45, Nguyễn Trãi, Hà Nội', '00987654321', 'BHYT654321', 5, 3, '2026-05-15', 'AFTERNOON', 'Sẹo lồi vùng ngực sau mụn', 'PENDING', 3),
('KH202604280005', 'Lê Văn A', '0934567890', 'lea@email.com', '1988-03-10', 'MALE', 'Số 78, Xã Đàn, Hà Nội', '00555555555', NULL, 7, 4, '2026-05-08', 'MORNING', 'Chàm sữa cho bé 3 tuổi, da khô, bong tróc', 'COMPLETED', 4),
('KH202604280006', 'Lê Văn A', '0934567890', 'lea@email.com', '1988-03-10', 'MALE', 'Số 78, Xã Đàn, Hà Nội', '00555555555', NULL, 9, 5, '2026-05-20', 'AFTERNOON', 'Ngứa vùng bẹn, nổi mảng đỏ hình vòng cung', 'PENDING', 4),
('KH202604280007', 'Phạm Thị B', '0945678901', 'phamb@email.com', '2000-11-25', 'FEMALE', 'Số 23, Cầu Giấy, Hà Nội', '00666666666', 'BHYT999999', 3, 2, '2026-04-25', 'MORNING', 'Mụn trứng cá, thâm mụn nhiều', 'COMPLETED', 5),
('KH202604280008', 'Phạm Thị B', '0945678901', 'phamb@email.com', '2000-11-25', 'FEMALE', 'Số 23, Cầu Giấy, Hà Nội', '00666666666', 'BHYT999999', 4, 2, '2026-05-12', 'AFTERNOON', 'Điều trị sẹo rỗ sau mụn', 'COMPLETED', 5),
('KH202604280009', 'Nguyễn Văn Bệnh', '0912345678', 'benh@email.com', '1990-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội', '00123456789', 'BHYT123456', 2, 1, '2026-05-25', 'MORNING', 'Tái khám sau điều trị vảy nến', 'PENDING', 2),
('KH202604280010', 'Trần Thị Khỏe', '0923456789', 'khoe@email.com', '1995-08-20', 'FEMALE', 'Số 45, Nguyễn Trãi, Hà Nội', '00987654321', 'BHYT654321', 6, 3, '2026-05-18', 'MORNING', 'Phẫu thuật cắt bỏ nốt ruồi lớn vùng mặt', 'CONFIRMED', 3),
('KH202604280011', 'Nguyễn Thị Hồng', '0956789012', 'hong@email.com', '1992-07-12', 'FEMALE', 'Số 67, Tây Sơn, Hà Nội', NULL, NULL, 11, 6, '2026-05-06', 'AFTERNOON', 'Điều trị sẹo mụn bằng laser', 'COMPLETED', NULL),
('KH202604280012', 'Trần Văn Cường', '0967890123', 'cuong@email.com', '1985-12-01', 'MALE', 'Số 34, Hoàng Quốc Việt, Hà Nội', '00888888888', 'BHYT888888', 2, 1, '2026-05-22', 'MORNING', 'Nổi mảng trắng trên da, nghi bạch biến', 'PENDING', NULL),
('KH202604280013', 'Lê Thị Linh', '0978901234', 'linh@email.com', '2005-09-18', 'FEMALE', 'Số 9, Hoàng Hoa Thám, Hà Nội', NULL, NULL, 8, 4, '2026-04-28', 'MORNING', 'Phát ban, sốt nhẹ, nghi sởi hoặc tay chân miệng', 'PENDING', NULL),
('KH202604280014', 'Hoàng Văn Nam', '0989012345', 'nam@email.com', '1975-03-22', 'MALE', 'Số 56, Láng Hạ, Hà Nội', '00777777777', 'BHYT777777', 10, 5, '2026-05-30', 'MORNING', 'Nấm da vùng bẹn và đùi, ngứa nhiều', 'PENDING', NULL),
('KH202604280015', 'Đỗ Thị Hoa', '0990123456', 'hoa@email.com', '1998-06-30', 'FEMALE', 'Số 12, Trần Duy Hưng, Hà Nội', '00444444444', 'BHYT444444', 12, 6, '2026-05-14', 'AFTERNOON', 'Căng bóng da, tàn nhang vùng má', 'CONFIRMED', NULL);

-- ===== 5. BỆNH ÁN / KẾT QUẢ KHÁM (cho các lịch đã hoàn thành) =====
INSERT INTO medical_records (appointment_id, diagnosis, prescription, notes) VALUES
(1, 'Viêm da cơ địa cấp tính do dị ứng thời tiết', 'Loratadine 10mg x 10 ngày; Betamethasone 0.1% bôi 2 lần/ngày x 7 ngày; Kem dưỡng ẩm Ceradan', 'Bệnh nhân cần tránh tiếp xúc với phấn hoa, lông động vật, hạn chế gãi'),
(2, 'Nám da hỗn hợp (melasma)', 'Kem bôi Hydroquinone 4% + Tretinoin 0.05% tối; Kem chống nắng SPF 50+ mỗi sáng; Uống Tranexamic acid 250mg x 2 tháng', 'Tái khám sau 2 tháng, kiên trì điều trị ít nhất 6 tháng'),
(5, 'Chàm sữa (atopic dermatitis) ở trẻ 3 tuổi', 'Kem dưỡng ẩm Cetaphil bôi nhiều lần; Hydrocortisone 1% bôi vùng viêm; Fexofenadine 30mg x 7 ngày', 'Cho trẻ mặc quần áo cotton, tránh xà phòng có mùi, tắm nước ấm'),
(7, 'Mụn trứng cá độ II - III', 'Adapalene 0.1% tối; Clindamycin 1% sáng; Doxycycline 100mg x 14 ngày', 'Kiêng đồ ngọt, đồ cay nóng, tái khám sau 1 tháng'),
(8, 'Sẹo rỗ sau mụn', 'Điều trị bằng laser fractional CO2 3 lần, mỗi lần cách nhau 2 tháng; Kem phục hồi da B5', 'Đã thực hiện lần 1, tái khám sau 2 tháng'),
(11, 'Sẹo mụn lõm', 'Điều trị bằng laser CO2; Kem phục hồi; Chống nắng kỹ', 'Sau điều trị, da sẽ bong vảy trong 5-7 ngày');

-- ===== 6. HÓA ĐƠN (cho các lịch đã hoàn thành và đã thanh toán) =====
INSERT INTO invoices (appointment_id, total_amount, status, payment_date) VALUES
(1, 350000, 'PAID', '2026-05-01'),
(2, 450000, 'PAID', '2026-05-10'),
(5, 320000, 'PAID', '2026-05-08'),
(7, 280000, 'PAID', '2026-04-25'),
(8, 2500000, 'PAID', '2026-05-12'),
(11, 1800000, 'PAID', '2026-05-06');

-- ===== 7. BÀI VIẾT / TIN TỨC SỨC KHỎE (10 bài) =====
INSERT INTO articles (title, summary, content, category) VALUES
('Cách chăm sóc da mùa hè', 'Những lưu ý quan trọng để bảo vệ da dưới ánh nắng mặt trời mùa hè nóng bức', 'Nội dung chi tiết...', 'Chăm sóc da'),
('Bệnh vảy nến: Nguyên nhân, triệu chứng và phác đồ điều trị mới nhất', 'Cập nhật về bệnh vảy nến mãn tính và các phương pháp điều trị hiện đại như sinh học', 'Nội dung chi tiết...', 'Bệnh da liễu'),
('Trẻ hóa da bằng công nghệ Laser Genesis', 'Công nghệ Laser thế hệ mới giúp da săn chắc, mịn màng, không xâm lấn, không đau', 'Nội dung chi tiết...', 'Thẩm mỹ'),
('Chế độ ăn uống lành mạnh giúp da sáng khỏe', 'Những thực phẩm giàu vitamin C, E, omega-3 giúp da chống lão hóa, giảm mụn', 'Nội dung chi tiết...', 'Dinh dưỡng - Sức khỏe'),
('Điều trị sẹo rỗ bằng phương pháp lăn kim (Microneedling)', 'Microneedling kích thích sản sinh collagen, cải thiện sẹo rỗ và lỗ chân lông to', 'Nội dung chi tiết...', 'Thẩm mỹ'),
('Bệnh zona thần kinh: Dấu hiệu nhận biết và cách điều trị', 'Zona thần kinh gây đau rát, nổi mụn nước dọc dây thần kinh, dễ tái phát khi stress', 'Nội dung chi tiết...', 'Bệnh da liễu'),
('Lợi ích của kem chống nắng vật lý và hóa học', 'So sánh 2 loại kem chống nắng, hướng dẫn chọn sản phẩm phù hợp với từng loại da', 'Nội dung chi tiết...', 'Chăm sóc da'),
('Cấy chỉ điều trị nám, tàn nhang – phương pháp mới từ y học cổ truyền', 'Cấy chỉ dưới da giúp làm mờ nám, tàn nhang, da sáng hồng tự nhiên', 'Nội dung chi tiết...', 'Thẩm mỹ'),
('Rụng tóc từng mảng (Alopecia areata): Nguyên nhân và giải pháp', 'Rụng tóc từng mảng do rối loạn miễn dịch, có thể điều trị bằng corticoid và liệu pháp ánh sáng', 'Nội dung chi tiết...', 'Bệnh da liễu'),
('Phẫu thuật nội soi tạo hình vùng mặt sau chấn thương', 'Ứng dụng phẫu thuật nội soi trong tạo hình vùng mặt giúp ít sẹo, phục hồi nhanh', 'Nội dung chi tiết...', 'Phẫu thuật tạo hình');

-- =====================================================
-- KIỂM TRA DỮ LIỆU SAU KHI INSERT
-- =====================================================
SELECT 'DEPARTMENTS' as Table_Name, COUNT(*) as Records FROM departments
UNION SELECT 'DOCTORS', COUNT(*) FROM doctors
UNION SELECT 'USERS', COUNT(*) FROM users
UNION SELECT 'APPOINTMENTS', COUNT(*) FROM appointments
UNION SELECT 'MEDICAL_RECORDS', COUNT(*) FROM medical_records
UNION SELECT 'INVOICES', COUNT(*) FROM invoices
UNION SELECT 'ARTICLES', COUNT(*) FROM articles;

use hospital_db;


-- =====================================================
-- 1. THÊM TÀI KHOẢN BÁC SĨ (role = DOCTOR)
-- =====================================================
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address)
VALUES ('Bác sĩ Nguyễn Văn An', '0900000001', 'bs.an@hospital.com', '123456', 'DOCTOR', '1980-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội');

-- =====================================================
-- 2. THÊM BÁC SĨ VÀO BẢNG doctors (KHÔNG có user_id, chỉ có name và department_id)
-- =====================================================
INSERT INTO doctors (name, department_id, qualification, experience, fee)
VALUES ('Bác sĩ Nguyễn Văn An', 1, 'Tiến sĩ, Bác sĩ chính', 15, 350000);

-- =====================================================
-- 3. THÊM BỆNH NHÂN MỚI (role = USER)
-- =====================================================
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address) VALUES
('Nguyễn Thị Hồng', '0987654321', 'hong@email.com', '123456', 'USER', '1992-07-12', 'FEMALE', 'Số 67, Tây Sơn, Hà Nội'),
('Trần Văn Cường', '0976543210', 'cuong@email.com', '123456', 'USER', '1985-12-01', 'MALE', 'Số 34, Hoàng Quốc Việt, Hà Nội'),
('Lê Thị Linh', '0965432109', 'linh@email.com', '123456', 'USER', '2005-09-18', 'FEMALE', 'Số 9, Hoàng Hoa Thám, Hà Nội'),
('Hoàng Văn Nam', '0954321098', 'nam@email.com', '123456', 'USER', '1975-03-22', 'MALE', 'Số 56, Láng Hạ, Hà Nội'),
('Đỗ Thị Hoa', '0943210987', 'hoa@email.com', '123456', 'USER', '1998-06-30', 'FEMALE', 'Số 12, Trần Duy Hưng, Hà Nội');

-- =====================================================
-- 4. TẠO LỊCH HỌC CHO BÁC SĨ (doctor_id = id vừa thêm ở bước 2)
-- =====================================================
-- Lấy doctor_id của 'Bác sĩ Nguyễn Văn An' (thường là 13)
INSERT INTO appointments (appointment_code, patient_name, patient_phone, patient_email, patient_dob, patient_gender, patient_address, identity_no, insurance_no, doctor_id, department_id, appointment_date, slot, symptoms, status, user_id)
SELECT
CONCAT('KH', FLOOR(RAND() * 1000000000)),
u.fullname,
u.phone,
u.email,
u.dob,
u.gender,
u.address,
NULL,
NULL,
(SELECT id FROM doctors WHERE name = 'Bác sĩ Nguyễn Văn An' LIMIT 1),  -- doctor_id
1,  -- department_id
DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 14) + 1 DAY),
CASE WHEN RAND() > 0.5 THEN 'MORNING' ELSE 'AFTERNOON' END,
CASE
WHEN u.fullname = 'Nguyễn Thị Hồng' THEN 'Nổi mẩn đỏ khắp người, ngứa nhiều về đêm'
WHEN u.fullname = 'Trần Văn Cường' THEN 'Vảy nến vùng khuỷu tay và đầu gối'
WHEN u.fullname = 'Lê Thị Linh' THEN 'Mụn trứng cá nặng vùng mặt, có thâm'
WHEN u.fullname = 'Hoàng Văn Nam' THEN 'Nám da vùng má, da khô sạm'
ELSE 'Thăm khám da liễu định kỳ'
END,
CASE WHEN RAND() > 0.3 THEN 'PENDING' ELSE 'CONFIRMED' END,
u.id
FROM users u
WHERE u.phone IN ('0987654321', '0976543210', '0965432109', '0954321098', '0943210987');

-- =====================================================
-- 5. KIỂM TRA KẾT QUẢ
-- =====================================================
SELECT
a.id,
a.appointment_code,
a.patient_name,
a.patient_phone,
d.name as doctor_name,
a.appointment_date,
a.slot,
a.status
FROM appointments a
JOIN doctors d ON a.doctor_id = d.id
WHERE d.name = 'Bác sĩ Nguyễn Văn An'
ORDER BY a.appointment_date DESC;



-- Kiểm tra status hiện tại của các lịch hẹn này
SELECT id, patient_name, status FROM appointments
WHERE patient_phone IN ('0943210987', '0965432109', '0954321098', '0976543210', '0987654321');

-- Cập nhật status cho các lịch hẹn có status NULL hoặc rỗng
UPDATE appointments SET status = 'PENDING'
WHERE status IS NULL OR status = ''
AND patient_phone IN ('0943210987', '0965432109', '0954321098', '0976543210', '0987654321');

-- Hoặc cập nhật cụ thể từng bệnh nhân
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0965432109';  -- Lê Thị Linh
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0976543210';  -- Trần Văn Cường
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0987654321';  -- Nguyễn Thị Hồng


-- Xem các medical_record bị trùng
SELECT appointment_id, COUNT(*) FROM medical_records GROUP BY appointment_id HAVING COUNT(*) > 1;

-- Giữ lại 1 bản ghi, xóa bản trùng (cẩn thận)
DELETE FROM medical_records WHERE id NOT IN (
SELECT MIN(id) FROM medical_records GROUP BY appointment_id
);
-- Xóa hết medical_record cũ, để dữ liệu sạch
DELETE FROM medical_records;

SELECT id, name FROM doctors WHERE name LIKE '%Lê Văn Cường%';
-- Kết quả mong đợi: id = 3
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address, created_at, updated_at) VALUES
('Nguyễn Văn Anh', '0912345601', 'nguyenvananh1@gmail.com', '123456', 'USER', '1990-05-10', 'MALE', 'Hà Nội', NOW(), NOW()),
('Trần Thị Mai', '0912345602', 'tranthimai2@gmail.com', '123456', 'USER', '1985-08-15', 'FEMALE', 'Hà Nội', NOW(), NOW()),
('Lê Văn Hùng', '0912345603', 'levanhung3@gmail.com', '123456', 'USER', '2000-03-20', 'MALE', 'Hà Nội', NOW(), NOW()),
('Phạm Thị Hoa', '0912345604', 'phamthihoa4@gmail.com', '123456', 'USER', '1995-12-25', 'FEMALE', 'Hà Nội', NOW(), NOW()),
('Hoàng Văn Nam', '0912345605', 'hoangvannam5@gmail.com', '123456', 'USER', '1988-07-08', 'MALE', 'Hà Nội', NOW(), NOW()),
('Ngô Thị Lan', '0912345606', 'ngothilan6@gmail.com', '123456', 'USER', '1992-09-30', 'FEMALE', 'Hà Nội', NOW(), NOW()),
('Vũ Đức Minh', '0912345607', 'vuducminh7@gmail.com', '123456', 'USER', '1998-11-12', 'MALE', 'Hà Nội', NOW(), NOW()),
('Đỗ Thị Hương', '0912345608', 'dothihuong8@gmail.com', '123456', 'USER', '1993-04-18', 'FEMALE', 'Hà Nội', NOW(), NOW()),
('Bùi Văn Tùng', '0912345609', 'buivantung9@gmail.com', '123456', 'USER', '1980-02-22', 'MALE', 'Hà Nội', NOW(), NOW()),
('Trịnh Thị Lan Anh', '0912345610', 'trinhthilanh0@gmail.com', '123456', 'USER', '1997-06-14', 'FEMALE', 'Hà Nội', NOW(), NOW()),
('Phùng Văn Thắng', '0912345611', 'phungvanthang11@gmail.com', '123456', 'USER', '1991-10-05', 'MALE', 'Hà Nội', NOW(), NOW());


INSERT INTO appointments (appointment_code, patient_name, patient_phone, patient_email, patient_dob, patient_gender, patient_address, doctor_id, department_id, appointment_date, slot, symptoms, status, created_at, updated_at) VALUES
('KH2026042201', 'Vũ Đức Minh', '0912345607', 'vuducminh7@gmail.com', '1998-11-12', 'MALE', 'Hà Nội', 3, 2, '2026-04-22', 'MORNING', 'Zona thần kinh vùng lưng', 'COMPLETED', NOW(), NOW()),
('KH2026042202', 'Đỗ Thị Hương', '0912345608', 'dothihuong8@gmail.com', '1993-04-18', 'FEMALE', 'Hà Nội', 3, 2, '2026-04-22', 'AFTERNOON', 'Ghẻ, ngứa nhiều về đêm', 'COMPLETED', NOW(), NOW()),
('KH2026042301', 'Bùi Văn Tùng', '0912345609', 'buivantung9@gmail.com', '1980-02-22', 'MALE', 'Hà Nội', 3, 2, '2026-04-23', 'MORNING', 'Phát ban do dị ứng thuốc', 'COMPLETED', NOW(), NOW()),
('KH2026042302', 'Trịnh Thị Lan Anh', '0912345610', 'trinhthilanh0@gmail.com', '1997-06-14', 'FEMALE', 'Hà Nội', 3, 2, '2026-04-23', 'AFTERNOON', 'Mề đay mạn tính', 'COMPLETED', NOW(), NOW()),
('KH2026042401', 'Phùng Văn Thắng', '0912345611', 'phungvanthang11@gmail.com', '1991-10-05', 'MALE', 'Hà Nội', 3, 2, '2026-04-24', 'MORNING', 'Nám da, tàn nhang', 'COMPLETED', NOW(), NOW());

INSERT INTO appointments (appointment_code, patient_name, patient_phone, patient_email, patient_dob, patient_gender, patient_address, doctor_id, department_id, appointment_date, slot, symptoms, status, created_at, updated_at) VALUES
('KH2026050301', 'Nguyễn Văn Anh', '0912345601', 'nguyenvananh1@gmail.com', '1990-05-10', 'MALE', 'Hà Nội', 3, 2, '2026-05-03', 'MORNING', 'Nổi mẩn đỏ khắp người, ngứa nhiều', 'PENDING', NOW(), NOW()),
('KH2026050401', 'Trần Thị Mai', '0912345602', 'tranthimai2@gmail.com', '1985-08-15', 'FEMALE', 'Hà Nội', 3, 2, '2026-05-04', 'MORNING', 'Mụn trứng cá nặng, viêm mủ', 'PENDING', NOW(), NOW()),
('KH2026050501', 'Lê Văn Hùng', '0912345603', 'levanhung3@gmail.com', '2000-03-20', 'MALE', 'Hà Nội', 3, 2, '2026-05-05', 'MORNING', 'Vảy nến vùng da đầu, bong tróc', 'PENDING', NOW(), NOW()),
('KH2026050601', 'Phạm Thị Hoa', '0912345604', 'phamthihoa4@gmail.com', '1995-12-25', 'FEMALE', 'Hà Nội', 3, 2, '2026-05-06', 'MORNING', 'Nấm da vùng bẹn', 'PENDING', NOW(), NOW()),
('KH2026050701', 'Hoàng Văn Nam', '0912345605', 'hoangvannam5@gmail.com', '1988-07-08', 'MALE', 'Hà Nội', 3, 2, '2026-05-07', 'MORNING', 'Sẹo lồi sau mụn', 'PENDING', NOW(), NOW()),
('KH2026050801', 'Ngô Thị Lan', '0912345606', 'ngothilan6@gmail.com', '1992-09-30', 'FEMALE', 'Hà Nội', 3, 2, '2026-05-08', 'MORNING', 'Viêm da cơ địa tái phát', 'PENDING', NOW(), NOW());


-- 1. Lấy ID appointment của từng người
SELECT id, patient_name FROM appointments WHERE doctor_id = 3 AND status = 'COMPLETED' AND appointment_date >= '2026-04-22';

-- 2. UPDATE cho từng người (thay ? bằng ID thực tế)
UPDATE medical_records
SET diagnosis = 'Viêm da cơ địa tái phát',
prescription = 'Cetirizine 10mg x 10 viên, Kem bôi Betamethasone 0.1%',
notes = 'Tránh thức ăn tanh, hải sản, tái khám sau 7 ngày'
WHERE appointment_id = ?;

UPDATE medical_records
SET diagnosis = 'Nấm da vùng bẹn',
prescription = 'Clotrimazole 1% bôi 2 lần/ngày x 14 ngày, Terbinafine 250mg x 14 ngày',
notes = 'Vệ sinh khô ráo, tái khám sau 2 tuần'
WHERE appointment_id = ?;

UPDATE medical_records
SET diagnosis = 'Mề đay mạn tính',
prescription = 'Fexofenadine 180mg x 20 viên, uống 1 viên/ngày',
notes = 'Tránh tác nhân gây dị ứng'
WHERE appointment_id = ?;

UPDATE medical_records
SET diagnosis = 'Viêm nang lông',
prescription = 'Clindamycin 1% bôi sáng tối, Doxycycline 100mg x 14 ngày',
notes = 'Vệ sinh da, tránh cạo lông'
WHERE appointment_id = ?;



INSERT INTO medical_records (appointment_id, diagnosis, prescription, notes, created_at, updated_at)
SELECT
a.id,
CASE a.patient_name
WHEN 'Vũ Đức Minh' THEN 'Viêm da cơ địa tái phát'
WHEN 'Đỗ Thị Hương' THEN 'Nấm da vùng bẹn'
WHEN 'Bùi Văn Tùng' THEN 'Mề đay mạn tính'
WHEN 'Trịnh Thị Lan Anh' THEN 'Viêm nang lông'
WHEN 'Phùng Văn Thắng' THEN 'Viêm họng cấp tính'
END,
CASE a.patient_name
WHEN 'Vũ Đức Minh' THEN 'Cetirizine 10mg x 10 viên, Kem bôi Betamethasone 0.1%'
WHEN 'Đỗ Thị Hương' THEN 'Clotrimazole 1% bôi 2 lần/ngày x 14 ngày'
WHEN 'Bùi Văn Tùng' THEN 'Fexofenadine 180mg x 20 viên'
WHEN 'Trịnh Thị Lan Anh' THEN 'Clindamycin 1% bôi, Doxycycline 100mg x 14 ngày'
WHEN 'Phùng Văn Thắng' THEN 'Amoxicillin 500mg, Paracetamol 500mg, Alpha Choay'
END,
'Tái khám nếu không đỡ',
NOW(),
NOW()
FROM appointments a
WHERE a.doctor_id = 3 AND a.status = 'COMPLETED'
ON DUPLICATE KEY UPDATE
diagnosis = VALUES(diagnosis),
prescription = VALUES(prescription);





-- Xem các appointment_id bị trùng medical_record
SELECT appointment_id, COUNT(*) FROM medical_records GROUP BY appointment_id HAVING COUNT(*) > 1;

-- Xóa tất cả medical_records
DELETE FROM medical_records;

-- Hoặc xóa theo từng appointment_id bị trùng
DELETE FROM medical_records WHERE appointment_id IN (15, 16, 17, 18, 19);




INSERT INTO medical_records (appointment_id, diagnosis, prescription, notes, created_at, updated_at)
SELECT
a.id,
CASE a.patient_name
WHEN 'Vũ Đức Minh' THEN 'Viêm da cơ địa tái phát'
WHEN 'Đỗ Thị Hương' THEN 'Nấm da vùng bẹn'
WHEN 'Bùi Văn Tùng' THEN 'Mề đay mạn tính'
WHEN 'Trịnh Thị Lan Anh' THEN 'Viêm nang lông'
WHEN 'Phùng Văn Thắng' THEN 'Viêm họng cấp tính'
END,
CASE a.patient_name
WHEN 'Vũ Đức Minh' THEN 'Cetirizine 10mg x 10 viên, Kem bôi Betamethasone 0.1%'
WHEN 'Đỗ Thị Hương' THEN 'Clotrimazole 1% bôi 2 lần/ngày x 14 ngày'
WHEN 'Bùi Văn Tùng' THEN 'Fexofenadine 180mg x 20 viên'
WHEN 'Trịnh Thị Lan Anh' THEN 'Clindamycin 1% bôi, Doxycycline 100mg x 14 ngày'
WHEN 'Phùng Văn Thắng' THEN 'Amoxicillin 500mg, Paracetamol 500mg, Alpha Choay'
END,
'Tái khám nếu không đỡ',
NOW(),
NOW()
FROM appointments a
WHERE a.doctor_id = 3
AND a.status = 'COMPLETED'
AND a.id NOT IN (SELECT appointment_id FROM medical_records WHERE appointment_id IS NOT NULL);




INSERT INTO medical_records (appointment_id, diagnosis, prescription, notes, created_at, updated_at)
SELECT
a.id,
'Viêm họng cấp tính, Amidan sưng nề mủ nhẹ',
'Amoxicillin 500mg: 20 viên (uống 2 lần, mỗi lần 1 viên sau ăn). Paracetamol 500mg: 10 viên (uống khi sốt trên 38.5 độ). Alpha Choay: 2 vỉ (ngậm dưới lưỡi ngày 4 viên chia 2 lần)',
'Tái khám sau 5 ngày nếu không đỡ',
NOW(),
NOW()
FROM appointments a
WHERE a.patient_name = 'Vũ Hoàng Nhi'
AND a.status = 'COMPLETED'
AND a.id NOT IN (SELECT appointment_id FROM medical_records WHERE appointment_id IS NOT NULL);


-- Xóa medical_records trước (nếu có)
DELETE FROM medical_records WHERE appointment_id IN (
SELECT id FROM appointments WHERE patient_name = 'Vũ Hoàng Nhi'
);

-- Xóa appointments
DELETE FROM appointments WHERE patient_name = 'Vũ Hoàng Nhi';

-- Xóa user
DELETE FROM users WHERE fullname = 'Vũ Hoàng Nhi';


use hospital_db;


-- =====================================================
-- 1. THÊM TÀI KHOẢN BÁC SĨ (role = DOCTOR)
-- =====================================================
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address)
VALUES ('Bác sĩ Nguyễn Văn An', '0900000001', 'bs.an@hospital.com', '123456', 'DOCTOR', '1980-05-15', 'MALE', 'Số 12, Lê Lợi, Hà Nội');

-- =====================================================
-- 2. THÊM BÁC SĨ VÀO BẢNG doctors (KHÔNG có user_id, chỉ có name và department_id)
-- =====================================================
INSERT INTO doctors (name, department_id, qualification, experience, fee)
VALUES ('Bác sĩ Nguyễn Văn An', 1, 'Tiến sĩ, Bác sĩ chính', 15, 350000);

-- =====================================================
-- 3. THÊM BỆNH NHÂN MỚI (role = USER)
-- =====================================================
INSERT INTO users (fullname, phone, email, password, role, dob, gender, address) VALUES
('Nguyễn Thị Hồng', '0987654321', 'hong@email.com', '123456', 'USER', '1992-07-12', 'FEMALE', 'Số 67, Tây Sơn, Hà Nội'),
('Trần Văn Cường', '0976543210', 'cuong@email.com', '123456', 'USER', '1985-12-01', 'MALE', 'Số 34, Hoàng Quốc Việt, Hà Nội'),
('Lê Thị Linh', '0965432109', 'linh@email.com', '123456', 'USER', '2005-09-18', 'FEMALE', 'Số 9, Hoàng Hoa Thám, Hà Nội'),
('Hoàng Văn Nam', '0954321098', 'nam@email.com', '123456', 'USER', '1975-03-22', 'MALE', 'Số 56, Láng Hạ, Hà Nội'),
('Đỗ Thị Hoa', '0943210987', 'hoa@email.com', '123456', 'USER', '1998-06-30', 'FEMALE', 'Số 12, Trần Duy Hưng, Hà Nội');

-- =====================================================
-- 4. TẠO LỊCH HỌC CHO BÁC SĨ (doctor_id = id vừa thêm ở bước 2)
-- =====================================================
-- Lấy doctor_id của 'Bác sĩ Nguyễn Văn An' (thường là 13)
INSERT INTO appointments (appointment_code, patient_name, patient_phone, patient_email, patient_dob, patient_gender, patient_address, identity_no, insurance_no, doctor_id, department_id, appointment_date, slot, symptoms, status, user_id)
SELECT
CONCAT('KH', FLOOR(RAND() * 1000000000)),
u.fullname,
u.phone,
u.email,
u.dob,
u.gender,
u.address,
NULL,
NULL,
(SELECT id FROM doctors WHERE name = 'Bác sĩ Nguyễn Văn An' LIMIT 1),  -- doctor_id
1,  -- department_id
DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 14) + 1 DAY),
CASE WHEN RAND() > 0.5 THEN 'MORNING' ELSE 'AFTERNOON' END,
CASE
WHEN u.fullname = 'Nguyễn Thị Hồng' THEN 'Nổi mẩn đỏ khắp người, ngứa nhiều về đêm'
WHEN u.fullname = 'Trần Văn Cường' THEN 'Vảy nến vùng khuỷu tay và đầu gối'
WHEN u.fullname = 'Lê Thị Linh' THEN 'Mụn trứng cá nặng vùng mặt, có thâm'
WHEN u.fullname = 'Hoàng Văn Nam' THEN 'Nám da vùng má, da khô sạm'
ELSE 'Thăm khám da liễu định kỳ'
END,
CASE WHEN RAND() > 0.3 THEN 'PENDING' ELSE 'CONFIRMED' END,
u.id
FROM users u
WHERE u.phone IN ('0987654321', '0976543210', '0965432109', '0954321098', '0943210987');

-- =====================================================
-- 5. KIỂM TRA KẾT QUẢ
-- =====================================================
SELECT
a.id,
a.appointment_code,
a.patient_name,
a.patient_phone,
d.name as doctor_name,
a.appointment_date,
a.slot,
a.status
FROM appointments a
JOIN doctors d ON a.doctor_id = d.id
WHERE d.name = 'Bác sĩ Nguyễn Văn An'
ORDER BY a.appointment_date DESC;



-- Kiểm tra status hiện tại của các lịch hẹn này
SELECT id, patient_name, status FROM appointments
WHERE patient_phone IN ('0943210987', '0965432109', '0954321098', '0976543210', '0987654321');

-- Cập nhật status cho các lịch hẹn có status NULL hoặc rỗng
UPDATE appointments SET status = 'PENDING'
WHERE status IS NULL OR status = ''
AND patient_phone IN ('0943210987', '0965432109', '0954321098', '0976543210', '0987654321');

-- Hoặc cập nhật cụ thể từng bệnh nhân
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0965432109';  -- Lê Thị Linh
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0976543210';  -- Trần Văn Cường
UPDATE appointments SET status = 'PENDING' WHERE patient_phone = '0987654321';  -- Nguyễn Thị Hồng


-- Xem các medical_record bị trùng
SELECT appointment_id, COUNT(*) FROM medical_records GROUP BY appointment_id HAVING COUNT(*) > 1;

-- Giữ lại 1 bản ghi, xóa bản trùng (cẩn thận)
DELETE FROM medical_records WHERE id NOT IN (
SELECT MIN(id) FROM medical_records GROUP BY appointment_id
);
-- Xóa hết medical_record cũ, để dữ liệu sạch
DELETE FROM medical_records;

-- Sau đó khám lại bệnh nhân từ đầu
-- Xóa các lịch hẹn theo appointment_code
DELETE FROM appointments
WHERE appointment_code IN (
'KH177736605699819E4',
'KH177736620475902E9',
'KH17774379711811FCF',
'KH17774537233403BE8'
);

-- Xóa 3 bác sĩ theo ID
DELETE FROM doctors
WHERE id IN (11, 12, 13);

-- Bước 1: Kiểm tra xem 3 bác sĩ có lịch hẹn không
SELECT * FROM appointments WHERE doctor_id IN (11, 12, 13);

-- Bước 2: Chuyển lịch hẹn của 3 bác sĩ sang bác sĩ ID 4 (TS.BS Phạm Thị Dung)
-- (bạn có thể đổi ID khác nếu muốn)
UPDATE appointments SET doctor_id = 4 WHERE doctor_id IN (11, 12, 13);

-- Bước 3: Kiểm tra lại lịch hẹn đã được chuyển chưa
SELECT * FROM appointments WHERE doctor_id = 4;

-- Bước 4: Xóa 3 bác sĩ
DELETE FROM doctors WHERE id IN (11, 12, 13);

-- Bước 5: Kiểm tra lại danh sách bác sĩ
SELECT * FROM doctors;

-- Giữ lại các ID cụ thể (ví dụ: giữ 1,2,3,4,5,6,7,8)
DELETE FROM appointments
WHERE id NOT IN (1, 2, 3, 4, 5, 6, 7, 8);

-- Tắt kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa lịch hẹn
DELETE FROM appointments
WHERE id NOT IN (
SELECT id FROM (
SELECT id FROM appointments
ORDER BY appointment_date DESC
LIMIT 8
) AS temp
);

-- Bật lại kiểm tra
SET FOREIGN_KEY_CHECKS = 1;


-- Xóa theo số điện thoại
DELETE FROM users WHERE phone IN ('0981234561', '0981234562', '0981234563', '0981234564', '0981234565');

-- Hoặc xóa theo email
DELETE FROM users WHERE email LIKE '%@dalieu.vn';

-- Hoặc xóa theo role DOCTOR (cẩn thận nếu có dữ liệu real)
DELETE FROM users WHERE role = 'DOCTOR';

-- Nếu password để plain text (KHÔNG KHUYẾN KHÍCH)
INSERT INTO users (fullname, phone, email, password, role, created_at, updated_at)
VALUES
('PGS.TS Nguyễn Văn An', '0981234561', 'nguyenvanan@dalieu.vn', '123456', 'DOCTOR', NOW(), NOW()),
('TS.BS Trần Thị Bình', '0981234562', 'tranthibinh@dalieu.vn', '123456', 'DOCTOR', NOW(), NOW()),
('BS.CKII Lê Văn Cường', '0981234563', 'levancuong@dalieu.vn', '123456', 'DOCTOR', NOW(), NOW());


