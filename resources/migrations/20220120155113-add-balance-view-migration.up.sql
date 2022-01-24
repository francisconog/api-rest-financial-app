CREATE OR REPLACE VIEW balance AS
    SELECT i.description,
           i.value,
           i.date,
           EXTRACT(MONTH FROM i.date) AS month, 
           EXTRACT(YEAR FROM i.date) AS year,
           'income' AS type
    FROM income i
    UNION ALL
    SELECT o.description,
           -o.value,
           o.date,
           EXTRACT(MONTH FROM o.date) AS month, 
           EXTRACT(YEAR FROM o.date) AS year,
           'outcome' AS type
    FROM outcome o
    ORDER BY date;