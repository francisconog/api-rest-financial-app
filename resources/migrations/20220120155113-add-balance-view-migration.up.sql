CREATE OR REPLACE VIEW balance AS
    SELECT i.description,
           i.value,
           i.date,
           'income' AS type
    FROM income i
    UNION ALL
    SELECT o.description,
           -o.value,
           o.date,
           'outcome' AS type
    FROM outcome o
    ORDER BY date;