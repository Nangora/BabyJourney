ALTER TABLE contents ADD kind VARCHAR(20) NOT NULL CONSTRAINT df_contents_kind DEFAULT 'LESSON';
GO
ALTER TABLE contents ADD category VARCHAR(30) NULL;
GO
ALTER TABLE contents ADD duration_min INT NULL;
GO
ALTER TABLE contents ADD thumbnail_url NVARCHAR(500) NULL;
GO
ALTER TABLE contents ADD body NVARCHAR(MAX) NULL;
GO
ALTER TABLE contents ADD tags NVARCHAR(200) NULL;
GO

-- Trạng thái học / lưu của từng người với từng nội dung
CREATE TABLE user_contents (
                               id BIGINT IDENTITY(1,1) PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               content_id BIGINT NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',   -- NOT_STARTED / IN_PROGRESS / COMPLETED
                               progress_percent INT NOT NULL DEFAULT 0,
                               saved BIT NOT NULL DEFAULT 0,
                               updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                               CONSTRAINT fk_uc_user FOREIGN KEY (user_id) REFERENCES users(id),
                               CONSTRAINT fk_uc_content FOREIGN KEY (content_id) REFERENCES contents(id),
                               CONSTRAINT uq_uc UNIQUE (user_id, content_id)
);
GO

-- Mỗi lần hoàn thành một hoạt động (để đếm số ngày có thực hành)
CREATE TABLE activity_logs (
                               id BIGINT IDENTITY(1,1) PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               content_id BIGINT NOT NULL,
                               completed_on DATE NOT NULL,
                               created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                               CONSTRAINT fk_al_user FOREIGN KEY (user_id) REFERENCES users(id),
                               CONSTRAINT fk_al_content FOREIGN KEY (content_id) REFERENCES contents(id)
);
GO
CREATE INDEX ix_al_user_date ON activity_logs(user_id, completed_on);
GO

-- Tâm trạng mỗi ngày (mỗi ngày một giá trị)
CREATE TABLE mood_entries (
                              id BIGINT IDENTITY(1,1) PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              entry_date DATE NOT NULL,
                              mood VARCHAR(20) NOT NULL,                            -- HAPPY / CALM / OKAY / LOW / ANXIOUS
                              CONSTRAINT fk_mood_user FOREIGN KEY (user_id) REFERENCES users(id),
                              CONSTRAINT uq_mood UNIQUE (user_id, entry_date)
);
GO

-- Nhật ký thai kỳ (riêng tư, chỉ chủ tài khoản đọc được)
CREATE TABLE journal_entries (
                                 id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 entry_date DATE NOT NULL,
                                 week_number INT NULL,
                                 title NVARCHAR(150) NOT NULL,
                                 body NVARCHAR(MAX) NOT NULL,
                                 mood VARCHAR(20) NULL,
                                 created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                                 CONSTRAINT fk_journal_user FOREIGN KEY (user_id) REFERENCES users(id)
);
GO
CREATE INDEX ix_journal_user_date ON journal_entries(user_id, entry_date);
GO

-- Nhắc việc cá nhân hiển thị trong lịch
CREATE TABLE reminders (
                           id BIGINT IDENTITY(1,1) PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           title NVARCHAR(150) NOT NULL,
                           note NVARCHAR(300) NULL,
                           remind_at DATETIME2 NOT NULL,
                           created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                           CONSTRAINT fk_reminder_user FOREIGN KEY (user_id) REFERENCES users(id)
);
GO
CREATE INDEX ix_reminder_user_time ON reminders(user_id, remind_at);
GO