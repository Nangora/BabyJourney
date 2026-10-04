ALTER TABLE pregnancies ADD goals VARCHAR(200) NULL;
GO

ALTER TABLE pregnancies ADD daily_minutes INT NULL;
GO

ALTER TABLE pregnancies ADD learning_styles VARCHAR(200) NULL;
GO

ALTER TABLE pregnancies ADD email_reminder BIT NOT NULL CONSTRAINT df_preg_email_reminder DEFAULT 0;
GO