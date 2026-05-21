-- Supabase relationship validation and guarded FK creation.
-- Run Section 1 first. Only run Section 3 after every validation query returns zero rows.

-- ============================================================
-- 1. Validate existing data before adding foreign keys
-- ============================================================

SELECT a.*
FROM articles a
LEFT JOIN users u ON a.author_id = u.id
WHERE a.author_id IS NOT NULL
AND u.id IS NULL;

SELECT c.*
FROM conversations c
LEFT JOIN users u ON c.assigned_agent_id = u.id
WHERE c.assigned_agent_id IS NOT NULL
AND u.id IS NULL;

SELECT c.*
FROM conversations c
LEFT JOIN users u ON c.registered_user_id = u.id
WHERE c.registered_user_id IS NOT NULL
AND u.id IS NULL;

SELECT m.*
FROM messages m
LEFT JOIN conversations c ON m.conversation_id = c.id
WHERE m.conversation_id IS NOT NULL
AND c.id IS NULL;

SELECT m.*
FROM messages m
LEFT JOIN users u ON m.sender_id = u.id
WHERE m.sender_id IS NOT NULL
AND u.id IS NULL;

SELECT crs.*
FROM conversation_read_states crs
LEFT JOIN conversations c ON crs.conversation_id = c.id
WHERE crs.conversation_id IS NOT NULL
AND c.id IS NULL;

SELECT crs.*
FROM conversation_read_states crs
LEFT JOIN users u ON crs.user_id = u.id
WHERE crs.user_id IS NOT NULL
AND u.id IS NULL;

SELECT ca.*
FROM career_applications ca
LEFT JOIN users u ON ca.user_id = u.id
WHERE ca.user_id IS NOT NULL
AND u.id IS NULL;

SELECT ca.*
FROM career_applications ca
LEFT JOIN users u ON ca.reviewed_by = u.id
WHERE ca.reviewed_by IS NOT NULL
AND u.id IS NULL;

SELECT p.*
FROM properties p
LEFT JOIN users u ON p.created_by = u.id
WHERE p.created_by IS NOT NULL
AND u.id IS NULL;

-- ============================================================
-- 2. Inspect which target foreign keys already exist
-- ============================================================

SELECT
    con.conname AS constraint_name,
    rel.relname AS table_name,
    string_agg(att.attname, ', ' ORDER BY cols.ordinality) AS columns,
    frel.relname AS referenced_table
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
JOIN pg_class frel ON frel.oid = con.confrelid
JOIN unnest(con.conkey) WITH ORDINALITY AS cols(attnum, ordinality) ON true
JOIN pg_attribute att ON att.attrelid = rel.oid AND att.attnum = cols.attnum
WHERE con.contype = 'f'
AND rel.relname IN (
    'articles',
    'conversations',
    'messages',
    'conversation_read_states',
    'career_applications',
    'properties'
)
AND att.attname IN (
    'author_id',
    'assigned_agent_id',
    'registered_user_id',
    'conversation_id',
    'sender_id',
    'user_id',
    'reviewed_by',
    'created_by'
)
GROUP BY con.conname, rel.relname, frel.relname
ORDER BY rel.relname, con.conname;

-- ============================================================
-- 3. Add only missing foreign key constraints
-- ============================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'articles'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'author_id'
    ) THEN
        ALTER TABLE articles
        ADD CONSTRAINT fk_articles_author
        FOREIGN KEY (author_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'conversations'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'assigned_agent_id'
    ) THEN
        ALTER TABLE conversations
        ADD CONSTRAINT fk_conversations_assigned_agent
        FOREIGN KEY (assigned_agent_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'conversations'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'registered_user_id'
    ) THEN
        ALTER TABLE conversations
        ADD CONSTRAINT fk_conversations_registered_user
        FOREIGN KEY (registered_user_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'messages'::regclass
        AND con.confrelid = 'conversations'::regclass
        AND att.attname = 'conversation_id'
    ) THEN
        ALTER TABLE messages
        ADD CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'messages'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'sender_id'
    ) THEN
        ALTER TABLE messages
        ADD CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'conversation_read_states'::regclass
        AND con.confrelid = 'conversations'::regclass
        AND att.attname = 'conversation_id'
    ) THEN
        ALTER TABLE conversation_read_states
        ADD CONSTRAINT fk_conversation_read_states_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'conversation_read_states'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'user_id'
    ) THEN
        ALTER TABLE conversation_read_states
        ADD CONSTRAINT fk_conversation_read_states_user
        FOREIGN KEY (user_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'career_applications'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'user_id'
    ) THEN
        ALTER TABLE career_applications
        ADD CONSTRAINT fk_career_applications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'career_applications'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'reviewed_by'
    ) THEN
        ALTER TABLE career_applications
        ADD CONSTRAINT fk_career_applications_reviewed_by
        FOREIGN KEY (reviewed_by)
        REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint con
        JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ANY(con.conkey)
        WHERE con.contype = 'f'
        AND con.conrelid = 'properties'::regclass
        AND con.confrelid = 'users'::regclass
        AND att.attname = 'created_by'
    ) THEN
        ALTER TABLE properties
        ADD CONSTRAINT fk_properties_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id);
    END IF;
END $$;
