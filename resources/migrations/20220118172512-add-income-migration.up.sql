CREATE TABLE income
(id SERIAL PRIMARY KEY,
description VARCHAR(100) NOT NULL,
value DECIMAL(11,2) NOT NULL DEFAULT 0,
date TIMESTAMP NOT NULL DEFAULT NOW()
);