-- File nay la schema Version 1 cho database BabyJourney (hoan toan moi,
-- tach biet voi schema cua VBooking). Vi dang chay tren 1 database trong
-- (babyjourney), Flyway se chay toan bo cac CREATE TABLE ben duoi 1 lan
-- duy nhat khi app khoi dong lan dau tren database nay.

-- Bang nguoi dung: dung chung cho ca me bau va bac si dang nhap he thong
CREATE TABLE users (
                       id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       full_name NVARCHAR(150) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       phone VARCHAR(20),
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',   -- USER (me bau) / DOCTOR / ADMIN
                       created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- Bang theo doi thai ky: 1 user co the co nhieu ban ghi thai ky qua cac lan
-- (nhung thuong dung se chi dung ban ghi moi nhat/active)
CREATE TABLE pregnancies (
                             id BIGINT IDENTITY(1,1) PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             due_date DATE NOT NULL,                     -- ngay du sinh, dung de tinh tuan thai hien tai
                             last_period_date DATE,                      -- ngay dau ky kinh cuoi, co the dung de tinh tuan thai chinh xac hon
                             baby_nickname NVARCHAR(100),                -- ten goi o nha cho be, khong bat buoc
                             created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                             CONSTRAINT fk_pregnancy_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Thu vien noi dung thai giao: nhac, truyen ke, bai tap hit tho...
-- phan loai theo khoang tuan thai phu hop
CREATE TABLE contents (
                          id BIGINT IDENTITY(1,1) PRIMARY KEY,
                          title NVARCHAR(200) NOT NULL,
                          type VARCHAR(20) NOT NULL,                  -- MUSIC / STORY / EXERCISE / ARTICLE
                          week_from INT NOT NULL,
                          week_to INT NOT NULL,
                          media_url NVARCHAR(500),                    -- link file nhac/video luu tren Blob Storage (tich hop o tuan sau)
                          description NVARCHAR(MAX),
                          created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- Danh sach bac si / phong kham co the dat lich
CREATE TABLE doctors (
                         id BIGINT IDENTITY(1,1) PRIMARY KEY,
                         full_name NVARCHAR(150) NOT NULL,
                         specialty NVARCHAR(150),                    -- chuyen khoa, vd: San khoa
                         clinic_name NVARCHAR(200),
                         clinic_address NVARCHAR(300),
                         price_per_session DECIMAL(12,2) NOT NULL,
                         created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- Lich hen kham: noi 1 user voi 1 doctor tai 1 thoi diem cu the
CREATE TABLE appointments (
                              id BIGINT IDENTITY(1,1) PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              doctor_id BIGINT NOT NULL,
                              appointment_time DATETIME2 NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING / CONFIRMED / CANCELLED
                              notes NVARCHAR(MAX),
                              created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                              CONSTRAINT fk_appointment_user FOREIGN KEY (user_id) REFERENCES users(id),
                              CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Bai dang trong cong dong me bau
CREATE TABLE posts (
                       id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       title NVARCHAR(200) NOT NULL,
                       content NVARCHAR(MAX) NOT NULL,
                       created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                       CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Binh luan duoi bai dang
CREATE TABLE comments (
                          id BIGINT IDENTITY(1,1) PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          content NVARCHAR(MAX) NOT NULL,
                          created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                          CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id),
                          CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id)
);

