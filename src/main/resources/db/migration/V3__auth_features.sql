ALTER TABLE users ADD is_active BIT NOT NULL CONSTRAINT df_users_is_active DEFAULT 1;
GO

ALTER TABLE users ADD token_version INT NOT NULL CONSTRAINT df_users_token_version DEFAULT 0;
GO

ALTER TABLE users ADD google_id VARCHAR(100) NULL;
GO

CREATE UNIQUE INDEX uq_users_google_id ON users(google_id) WHERE google_id IS NOT NULL;
GO

CREATE TABLE password_reset_tokens (
                                       id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       token_hash VARCHAR(64) NOT NULL,
                                       expires_at DATETIME2 NOT NULL,
                                       used BIT NOT NULL DEFAULT 0,
                                       CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

CREATE INDEX ix_prt_token_hash ON password_reset_tokens(token_hash);
GO