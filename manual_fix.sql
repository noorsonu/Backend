-- Manual fix for comments table
ALTER TABLE comments ALTER COLUMN user_id DROP NOT NULL;

-- Verify the change
\d comments;