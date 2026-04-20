CREATE DATABASE IF NOT EXISTS smart_healthcare;
USE smart_healthcare;

-- ======================================================
-- 1. bảng users (đã gộp thông tin profile)
-- ======================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL COMMENT 'Họ và tên đầy đủ',
    gender ENUM('male', 'female', 'other') DEFAULT 'other',
    date_of_birth DATE NULL,
    identity_number_hash VARCHAR(255) NULL COMMENT 'Hash CCCD/CMND',
    ethnicity VARCHAR(50) NULL COMMENT 'Dân tộc',
    province VARCHAR(100) NULL COMMENT 'Tỉnh/Thành phố',
    district VARCHAR(100) NULL COMMENT 'Quận/Huyện',
    ward VARCHAR(100) NULL COMMENT 'Phường/Xã',
    street_address TEXT NULL COMMENT 'Địa chỉ chi tiết',
    insurance_number VARCHAR(50) NULL COMMENT 'Số thẻ BHYT',
    avatar_url TEXT NULL COMMENT 'Ảnh đại diện',
    patient_code VARCHAR(50) UNIQUE COMMENT 'Mã bệnh nhân: PAT-xxxxxx',
    role ENUM('patient', 'doctor', 'admin', 'staff') NOT NULL DEFAULT 'patient',
    status ENUM('active', 'locked', 'inactive', 'pending_verification') DEFAULT 'pending_verification',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_role (role),
    INDEX idx_status (status),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_patient_code (patient_code),
    INDEX idx_full_name (full_name),
    INDEX idx_insurance_number (insurance_number)
);

-- ======================================================
-- 2. bảng otp_verifications (xác thực OTP qua SMS/Email)
-- ======================================================
CREATE TABLE otp_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20),
    email VARCHAR(100),
    otp_code CHAR(6) NOT NULL,
    purpose ENUM('register', 'forgot_password', 'login') DEFAULT 'login',
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_phone_otp (phone, otp_code),
    INDEX idx_email_otp (email, otp_code),
    INDEX idx_expires_at (expires_at)
);

-- ======================================================
-- 3. bảng specialties (chuyên khoa)
-- ======================================================
CREATE TABLE specialties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active'
);

-- ======================================================
-- 4. bảng consultation_types (loại khám)
-- ======================================================
CREATE TABLE consultation_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(12,0) NOT NULL DEFAULT 0,
    status ENUM('active', 'inactive') DEFAULT 'active'
);

-- ======================================================
-- 5. bảng doctors (thông tin bác sĩ)
-- ======================================================
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    specialty_id BIGINT NOT NULL,
    consultation_type_id BIGINT NOT NULL,
    doctor_code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Mã bác sĩ: BS-xxxxx',
    qualification VARCHAR(255) NOT NULL COMMENT 'Học vị, bằng cấp',
    years_of_experience INT DEFAULT 0,
    bio TEXT NULL,
    status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (consultation_type_id) REFERENCES consultation_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_specialty (specialty_id),
    INDEX idx_status (status),
    INDEX idx_doctor_code (doctor_code)
);

-- ======================================================
-- 6. bảng medicines (danh mục thuốc)
-- ======================================================
CREATE TABLE medicines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    unit VARCHAR(50) NULL COMMENT 'Viên, ống, chai, gói...',
    strength VARCHAR(100) NULL COMMENT 'Hàm lượng',
    stock_quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(12,0) NOT NULL DEFAULT 0,
    expiry_date DATE NULL,
    manufacturer VARCHAR(200) NULL,
    requires_prescription BOOLEAN DEFAULT TRUE,
    status ENUM('active', 'inactive') DEFAULT 'active',

    INDEX idx_code (code),
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_stock (stock_quantity),
    INDEX idx_expiry (expiry_date)
);

-- ======================================================
-- 7. bảng shifts (ca khám)
-- ======================================================
CREATE TABLE shifts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shift_name VARCHAR(50) NOT NULL COMMENT 'Ca sáng, ca chiều, ca tối',
    start_time TIME NOT NULL
);

-- ======================================================
-- 8. bảng appointments (lịch hẹn khám)
-- ======================================================
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    shift_id BIGINT NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT NULL COMMENT 'Lý do khám',
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    total_fee DECIMAL(12,0) DEFAULT 0,
    payment_method ENUM('cash', 'vnpay', 'momo', 'banking') NULL,
    payment_transaction_id VARCHAR(100) NULL,
    patient_note TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    UNIQUE KEY uk_doctor_slot (doctor_id, appointment_date, appointment_time),
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_date (appointment_date),
    INDEX idx_status (status),
    INDEX idx_booking_code (booking_code)
);

-- ======================================================
-- 9. bảng medical_records (bệnh án)
-- ======================================================
CREATE TABLE medical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_code VARCHAR(20) NOT NULL UNIQUE,
    appointment_id BIGINT NOT NULL UNIQUE,
    symptoms TEXT NULL,
    diagnosis TEXT NOT NULL,
    conclusion TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_appointment (appointment_id),
    INDEX idx_record_code (record_code)
);

-- ======================================================
-- 10. bảng prescriptions (đơn thuốc)
-- ======================================================
CREATE TABLE prescriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prescription_code VARCHAR(20) NOT NULL UNIQUE,
    medical_record_id BIGINT NOT NULL,
    status ENUM('pending', 'dispensed') DEFAULT 'pending',
    total_amount DECIMAL(12,0) DEFAULT 0,
    note TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_medical_record (medical_record_id),
    INDEX idx_status (status),
    INDEX idx_prescription_code (prescription_code)
);

-- ======================================================
-- 11. bảng prescription_details (chi tiết đơn thuốc)
-- ======================================================
CREATE TABLE prescription_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    dosage VARCHAR(100) NULL,
    duration INT NULL COMMENT 'Số ngày dùng',
    instruction TEXT NULL,

    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_prescription (prescription_id),
    INDEX idx_medicine (medicine_id)
);












-- ======================================================
-- XÓA TOÀN BỘ DỮ LIỆU CŨ
-- ======================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE prescription_details;
TRUNCATE TABLE prescriptions;
TRUNCATE TABLE medical_records;
TRUNCATE TABLE appointments;
TRUNCATE TABLE otp_verifications;
TRUNCATE TABLE doctors;
TRUNCATE TABLE users;
TRUNCATE TABLE specialties;
TRUNCATE TABLE consultation_types;
TRUNCATE TABLE shifts;
TRUNCATE TABLE medicines;
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================
-- INSERT DỮ LIỆU MỚI
-- ======================================================

-- 1. USERS (Admin, Doctors, Patients)
INSERT INTO users (username, password, email, phone, full_name, gender, date_of_birth, insurance_number, avatar_url, patient_code, role, status, created_at) VALUES
-- Admin
('admin', 'admin123', 'admin@smartcare.com', '0988888888', 'Quản trị viên hệ thống', 'male', '1990-01-01', NULL, 'https://randomuser.me/api/portraits/men/1.jpg', NULL, 'admin', 'active', NOW()),

-- Bác sĩ
('doctor.nguyen', '123456', 'bs.nguyen@smartcare.com', '0912345001', 'BS. Nguyễn Văn An', 'male', '1985-06-15', NULL, 'https://randomuser.me/api/portraits/men/2.jpg', NULL, 'doctor', 'active', NOW()),
('doctor.tran', '123456', 'bs.tran@smartcare.com', '0912345002', 'BS. Trần Thị Bình', 'female', '1990-03-20', NULL, 'https://randomuser.me/api/portraits/women/3.jpg', NULL, 'doctor', 'active', NOW()),
('doctor.le', '123456', 'bs.le@smartcare.com', '0912345003', 'BS. Lê Văn Cường', 'male', '1982-11-10', NULL, 'https://randomuser.me/api/portraits/men/4.jpg', NULL, 'doctor', 'active', NOW()),

-- Bệnh nhân (10 bệnh nhân)
('patient.huong', '123456', 'huong@email.com', '0912345101', 'Nguyễn Thị Hương', 'female', '1995-08-20', 'HT567890', 'https://randomuser.me/api/portraits/women/11.jpg', 'PAT-000001', 'patient', 'active', NOW()),
('patient.duc', '123456', 'duc@email.com', '0912345102', 'Trần Minh Đức', 'male', '2000-12-05', 'HT567891', 'https://randomuser.me/api/portraits/men/12.jpg', 'PAT-000002', 'patient', 'active', NOW()),
('patient.lan', '123456', 'lan@email.com', '0912345103', 'Phạm Thị Lan', 'female', '1988-03-15', 'HT567892', 'https://randomuser.me/api/portraits/women/13.jpg', 'PAT-000003', 'patient', 'active', NOW()),
('patient.hung', '123456', 'hung@email.com', '0912345104', 'Lê Văn Hùng', 'male', '1975-07-10', 'HT567893', 'https://randomuser.me/api/portraits/men/14.jpg', 'PAT-000004', 'patient', 'active', NOW()),
('patient.mai', '123456', 'mai@email.com', '0912345105', 'Đỗ Thị Mai', 'female', '1992-11-25', 'HT567894', 'https://randomuser.me/api/portraits/women/15.jpg', 'PAT-000005', 'patient', 'active', NOW()),
('patient.quang', '123456', 'quang@email.com', '0912345106', 'Hoàng Văn Quang', 'male', '1980-09-18', 'HT567895', 'https://randomuser.me/api/portraits/men/16.jpg', 'PAT-000006', 'patient', 'active', NOW()),
('patient.thao', '123456', 'thao@email.com', '0912345107', 'Vũ Thị Thảo', 'female', '1998-04-12', 'HT567896', 'https://randomuser.me/api/portraits/women/17.jpg', 'PAT-000007', 'patient', 'active', NOW()),
('patient.minh', '123456', 'minh@email.com', '0912345108', 'Phan Văn Minh', 'male', '1985-06-30', 'HT567897', 'https://randomuser.me/api/portraits/men/18.jpg', 'PAT-000008', 'patient', 'active', NOW()),
('patient.linh', '123456', 'linh@email.com', '0912345109', 'Trương Thị Linh', 'female', '2005-01-22', 'HT567898', 'https://randomuser.me/api/portraits/women/19.jpg', 'PAT-000009', 'patient', 'active', NOW()),
('patient.tuan', '123456', 'tuan@email.com', '0912345110', 'Đặng Văn Tuấn', 'male', '1990-10-08', 'HT567899', 'https://randomuser.me/api/portraits/men/20.jpg', 'PAT-000010', 'patient', 'active', NOW());

-- 2. SPECIALTIES (Chuyên khoa)
INSERT INTO specialties (name, description, status) VALUES
                                                        ('Tim mạch', 'Khám và điều trị các bệnh lý về tim mạch, huyết áp', 'active'),
                                                        ('Nhi khoa', 'Khám và điều trị bệnh cho trẻ em', 'active'),
                                                        ('Cơ xương khớp', 'Khám và điều trị các bệnh về xương khớp, cột sống', 'active');

-- 3. CONSULTATION_TYPES (Loại khám)
INSERT INTO consultation_types (name, price, status) VALUES
                                                         ('Khám cơ bản', 150000, 'active'),
                                                         ('Khám chuyên sâu', 300000, 'active');

-- 4. SHIFTS (Ca khám)
INSERT INTO shifts (shift_name, start_time) VALUES
                                                ('Ca sáng', '08:00:00'),
                                                ('Ca chiều', '13:30:00');

-- 5. DOCTORS (Thông tin bác sĩ)
INSERT INTO doctors (user_id, specialty_id, consultation_type_id, doctor_code, qualification, years_of_experience, bio, status) VALUES
                                                                                                                                    ((SELECT id FROM users WHERE username = 'doctor.nguyen'), (SELECT id FROM specialties WHERE name = 'Tim mạch'), (SELECT id FROM consultation_types WHERE name = 'Khám cơ bản'), 'BS001', 'Tiến sĩ - Bác sĩ chuyên khoa Tim mạch', 15, 'Chuyên gia tim mạch hàng đầu, đã điều trị thành công nhiều ca bệnh khó', 'active'),
                                                                                                                                    ((SELECT id FROM users WHERE username = 'doctor.tran'), (SELECT id FROM specialties WHERE name = 'Nhi khoa'), (SELECT id FROM consultation_types WHERE name = 'Khám chuyên sâu'), 'BS002', 'Thạc sĩ - Bác sĩ chuyên khoa Nhi', 10, 'Bác sĩ nhi khoa tận tâm, yêu trẻ', 'active'),
                                                                                                                                    ((SELECT id FROM users WHERE username = 'doctor.le'), (SELECT id FROM specialties WHERE name = 'Cơ xương khớp'), (SELECT id FROM consultation_types WHERE name = 'Khám cơ bản'), 'BS003', 'Bác sĩ chuyên khoa cấp I', 12, 'Chuyên điều trị các bệnh xương khớp, thoái hóa cột sống', 'active');

-- 6. MEDICINES (Thuốc)
INSERT INTO medicines (code, name, unit, strength, stock_quantity, unit_price, manufacturer, requires_prescription, status) VALUES
                                                                                                                                ('MED001', 'Paracetamol 500mg', 'Viên', '500mg', 1000, 5000, 'Công ty CPDP Hà Tây', TRUE, 'active'),
                                                                                                                                ('MED002', 'Amoxicillin 250mg', 'Gói', '250mg', 500, 12000, 'Công ty DP Mekophar', TRUE, 'active'),
                                                                                                                                ('MED003', 'Vitamin C 1000mg', 'Viên', '1000mg', 800, 8000, 'Công ty DP Dược phẩm TW', FALSE, 'active'),
                                                                                                                                ('MED004', 'Ibuprofen 400mg', 'Viên', '400mg', 300, 10000, 'Công ty DP Imexpharm', TRUE, 'active'),
                                                                                                                                ('MED005', 'Omeprazol 20mg', 'Viên', '20mg', 400, 15000, 'Công ty DP Stada', TRUE, 'active'),
                                                                                                                                ('MED006', 'Cetirizin 10mg', 'Viên', '10mg', 600, 7000, 'Công ty DP Dược Hậu Giang', FALSE, 'active');

-- 7. APPOINTMENTS PENDING (Lịch hẹn đang chờ - để hiển thị trong danh sách chờ khám của bác sĩ)
INSERT INTO appointments (booking_code, patient_id, doctor_id, appointment_date, shift_id, appointment_time, reason, status, total_fee, patient_note) VALUES
-- Lịch cho BS. Nguyễn Văn An (BS001)
('BK20250001', (SELECT id FROM users WHERE username = 'patient.huong'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), CURDATE(), 1, '08:00:00', 'Đau ngực, khó thở khi gắng sức', 'pending', 150000, 'Bệnh nhân có tiền sử huyết áp cao'),
('BK20250002', (SELECT id FROM users WHERE username = 'patient.duc'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), CURDATE(), 1, '09:00:00', 'Hồi hộp, tim đập nhanh', 'pending', 150000, NULL),
('BK20250003', (SELECT id FROM users WHERE username = 'patient.lan'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), CURDATE(), 2, '14:00:00', 'Đau thắt ngực, lan ra vai trái', 'pending', 300000, 'Đã từng bị nhồi máu cơ tim'),

-- Lịch cho BS. Trần Thị Bình (BS002)
('BK20250004', (SELECT id FROM users WHERE username = 'patient.quang'), (SELECT id FROM doctors WHERE doctor_code = 'BS002'), CURDATE(), 1, '08:00:00', 'Trẻ sốt cao 39°C, ho nhiều', 'pending', 150000, 'Bé 5 tuổi'),
('BK20250005', (SELECT id FROM users WHERE username = 'patient.thao'), (SELECT id FROM doctors WHERE doctor_code = 'BS002'), CURDATE(), 1, '09:30:00', 'Trẻ biếng ăn, chậm tăng cân', 'pending', 150000, 'Bé 8 tháng tuổi'),

-- Lịch cho BS. Lê Văn Cường (BS003)
('BK20250006', (SELECT id FROM users WHERE username = 'patient.tuan'), (SELECT id FROM doctors WHERE doctor_code = 'BS003'), CURDATE(), 2, '14:30:00', 'Đau lưng dưới, tê bì chân', 'pending', 150000, 'Đau lưng kéo dài 3 tháng');

-- 8. APPOINTMENTS COMPLETED (Lịch đã khám - để hiển thị trong danh sách bệnh nhân đã khám)
INSERT INTO appointments (booking_code, patient_id, doctor_id, appointment_date, shift_id, appointment_time, reason, status, total_fee, payment_method) VALUES
-- BS. Nguyễn Văn An
('BK20250010', (SELECT id FROM users WHERE username = 'patient.huong'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 1, '08:00:00', 'Đau ngực', 'completed', 150000, 'cash'),
('BK20250011', (SELECT id FROM users WHERE username = 'patient.duc'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), DATE_SUB(CURDATE(), INTERVAL 8 DAY), 1, '09:00:00', 'Khám tim mạch định kỳ', 'completed', 150000, 'banking'),
('BK20250012', (SELECT id FROM users WHERE username = 'patient.lan'), (SELECT id FROM doctors WHERE doctor_code = 'BS001'), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 2, '14:00:00', 'Cao huyết áp', 'completed', 300000, 'cash'),

-- BS. Trần Thị Bình
('BK20250013', (SELECT id FROM users WHERE username = 'patient.quang'), (SELECT id FROM doctors WHERE doctor_code = 'BS002'), DATE_SUB(CURDATE(), INTERVAL 9 DAY), 1, '08:00:00', 'Trẻ sốt cao', 'completed', 150000, 'cash'),
('BK20250014', (SELECT id FROM users WHERE username = 'patient.thao'), (SELECT id FROM doctors WHERE doctor_code = 'BS002'), DATE_SUB(CURDATE(), INTERVAL 6 DAY), 1, '09:30:00', 'Trẻ ho nhiều', 'completed', 150000, 'cash'),
('BK20250015', (SELECT id FROM users WHERE username = 'patient.minh'), (SELECT id FROM doctors WHERE doctor_code = 'BS002'), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 2, '13:30:00', 'Trẻ biếng ăn', 'completed', 150000, 'banking'),

-- BS. Lê Văn Cường
('BK20250016', (SELECT id FROM users WHERE username = 'patient.tuan'), (SELECT id FROM doctors WHERE doctor_code = 'BS003'), DATE_SUB(CURDATE(), INTERVAL 9 DAY), 2, '14:30:00', 'Đau lưng', 'completed', 150000, 'cash'),
('BK20250017', (SELECT id FROM users WHERE username = 'patient.huong'), (SELECT id FROM doctors WHERE doctor_code = 'BS003'), DATE_SUB(CURDATE(), INTERVAL 4 DAY), 2, '15:30:00', 'Đau khớp gối', 'completed', 150000, 'banking');

-- 9. MEDICAL_RECORDS (Bệnh án cho lịch đã khám)
INSERT INTO medical_records (record_code, appointment_id, symptoms, diagnosis, conclusion) VALUES
                                                                                               ('MR20250001', (SELECT id FROM appointments WHERE booking_code = 'BK20250010'), 'Đau ngực vùng trước tim, khó thở nhẹ', 'Rối loạn lo âu, tim mạch bình thường', 'Tư vấn giảm stress, tái khám sau 1 tháng'),
                                                                                               ('MR20250002', (SELECT id FROM appointments WHERE booking_code = 'BK20250011'), 'Khám định kỳ, không có triệu chứng bất thường', 'Sức khỏe tim mạch tốt', 'Tiếp tục chế độ ăn uống lành mạnh'),
                                                                                               ('MR20250003', (SELECT id FROM appointments WHERE booking_code = 'BK20250012'), 'Huyết áp 145/95, đau đầu vùng chẩm', 'Tăng huyết áp độ 1', 'Kê thuốc hạ áp, tái khám sau 1 tuần'),
                                                                                               ('MR20250004', (SELECT id FROM appointments WHERE booking_code = 'BK20250013'), 'Sốt 39°C, ho khan, mệt mỏi', 'Cúm A', 'Nghỉ ngơi, uống nhiều nước'),
                                                                                               ('MR20250005', (SELECT id FROM appointments WHERE booking_code = 'BK20250014'), 'Ho từng cơn, đau họng, sốt nhẹ', 'Viêm họng cấp do virus', 'Uống thuốc long đờm, súc miệng nước muối'),
                                                                                               ('MR20250006', (SELECT id FROM appointments WHERE booking_code = 'BK20250016'), 'Đau vùng thắt lưng, tăng khi cúi', 'Thoái hóa cột sống thắt lưng độ 1', 'Tập vật lý trị liệu, tránh mang vác nặng'),
                                                                                               ('MR20250007', (SELECT id FROM appointments WHERE booking_code = 'BK20250017'), 'Đau khớp gối trái khi đi lại', 'Thoái hóa khớp gối', 'Tiêm hyaluronic acid, tập bơi lội');

-- 10. PRESCRIPTIONS (Đơn thuốc)
INSERT INTO prescriptions (prescription_code, medical_record_id, status, total_amount, note) VALUES
                                                                                                 ('RX20250001', (SELECT id FROM medical_records WHERE record_code = 'MR20250003'), 'dispensed', 250000, 'Uống thuốc đều đặn mỗi ngày, đo huyết áp hàng ngày'),
                                                                                                 ('RX20250002', (SELECT id FROM medical_records WHERE record_code = 'MR20250004'), 'dispensed', 120000, 'Uống thuốc sau ăn, theo dõi nhiệt độ'),
                                                                                                 ('RX20250003', (SELECT id FROM medical_records WHERE record_code = 'MR20250006'), 'pending', 180000, 'Uống thuốc giảm đau khi đau nhiều'),
                                                                                                 ('RX20250004', (SELECT id FROM medical_records WHERE record_code = 'MR20250007'), 'pending', 350000, 'Tiêm theo lịch hẹn, uống thuốc bổ xương khớp');

-- 11. PRESCRIPTION_DETAILS (Chi tiết đơn thuốc)
INSERT INTO prescription_details (prescription_id, medicine_id, quantity, dosage, instruction) VALUES
                                                                                                   ((SELECT id FROM prescriptions WHERE prescription_code = 'RX20250001'), (SELECT id FROM medicines WHERE code = 'MED005'), 30, 'Ngày 1 viên sáng sau ăn', 'Uống vào buổi sáng, không bỏ thuốc'),
                                                                                                   ((SELECT id FROM prescriptions WHERE prescription_code = 'RX20250002'), (SELECT id FROM medicines WHERE code = 'MED001'), 10, 'Ngày 3-4 viên khi sốt', 'Uống khi sốt trên 38.5°C'),
                                                                                                   ((SELECT id FROM prescriptions WHERE prescription_code = 'RX20250002'), (SELECT id FROM medicines WHERE code = 'MED003'), 7, 'Ngày 1 viên', 'Bổ sung vitamin C để tăng sức đề kháng'),
                                                                                                   ((SELECT id FROM prescriptions WHERE prescription_code = 'RX20250003'), (SELECT id FROM medicines WHERE code = 'MED004'), 20, 'Ngày 2 viên sau ăn', 'Uống khi đau nhiều, không dùng quá 3 ngày'),
                                                                                                   ((SELECT id FROM prescriptions WHERE prescription_code = 'RX20250004'), (SELECT id FROM medicines WHERE code = 'MED006'), 30, 'Ngày 1 viên tối trước khi ngủ', 'Uống khi có triệu chứng đau khớp');

-- ======================================================
-- KIỂM TRA DỮ LIỆU
-- ======================================================
SELECT '✅ USERS:' as '', COUNT(*) FROM users;
SELECT '✅ SPECIALTIES:' as '', COUNT(*) FROM specialties;
SELECT '✅ CONSULTATION_TYPES:' as '', COUNT(*) FROM consultation_types;
SELECT '✅ DOCTORS:' as '', COUNT(*) FROM doctors;
SELECT '✅ MEDICINES:' as '', COUNT(*) FROM medicines;
SELECT '✅ SHIFTS:' as '', COUNT(*) FROM shifts;
SELECT '✅ APPOINTMENTS:' as '', COUNT(*) FROM appointments;
SELECT '✅ MEDICAL_RECORDS:' as '', COUNT(*) FROM medical_records;
SELECT '✅ PRESCRIPTIONS:' as '', COUNT(*) FROM prescriptions;
SELECT '✅ PRESCRIPTION_DETAILS:' as '', COUNT(*) FROM prescription_details;

-- ======================================================
-- HIỂN THỊ THÔNG TIN ĐĂNG NHẬP
-- ======================================================
SELECT '========== TÀI KHOẢN ĐĂNG NHẬP ==========' as '';
SELECT 'Admin: admin / admin123' as '';
SELECT 'Bác sĩ: doctor.nguyen / 123456' as '';
SELECT 'Bác sĩ: doctor.tran / 123456' as '';
SELECT 'Bác sĩ: doctor.le / 123456' as '';
SELECT 'Bệnh nhân: patient.huong / 123456' as '';