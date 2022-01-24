ALTER TABLE outcome
    ADD COLUMN category_id INTEGER NOT NULL DEFAULT 8,
    ADD CONSTRAINT fk_category
    FOREIGN KEY(category_id)
        REFERENCES category(id);