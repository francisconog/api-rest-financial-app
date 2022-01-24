CREATE TABLE category
(id SERIAL PRIMARY KEY,
 name VARCHAR(11) NOT NULL
);

INSERT INTO category (name)
VALUES 
    ('Alimentação'),
    ('Saúde'),
    ('Moradia'),
    ('Transporte'),
    ('Educação'),
    ('Lazer'),
    ('Imprevistos'),
    ('Outras');