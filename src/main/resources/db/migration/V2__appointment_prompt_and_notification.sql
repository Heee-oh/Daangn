ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS reminder_sent_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS trade_prompt_sent_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS trade_prompt_dismissed_at TIMESTAMP;
