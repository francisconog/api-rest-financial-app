-- :name list-balance-summary :? :*
-- :doc retrieves a list the summary of the balance
SELECT year, month, type, value 
 FROM balance
 WHERE year = 2021 AND month = 3
UNION ALL
SELECT year, month, 'total' AS type, SUM(value)
 FROM balance
 WHERE year = 2021 AND month = 3
 GROUP BY year, month
UNION ALL
SELECT EXTRACT(YEAR FROM date) AS year,
       EXTRACT(MONTH FROM date) AS month,
       category AS type,
       -SUM(value) AS value
 FROM outcome_category
 WHERE EXTRACT(YEAR FROM date) = 2021 AND 
       EXTRACT(MONTH FROM date) = 3
 GROUP BY category, year, month
ORDER BY year, month
