CREATE TABLE IF NOT EXISTS articles (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    cover_photo_url TEXT NOT NULL,
    cover_photo_path TEXT NOT NULL,
    author_id VARCHAR(255) NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_articles_latest_date
ON articles (COALESCE(updated_at, created_at) DESC);
