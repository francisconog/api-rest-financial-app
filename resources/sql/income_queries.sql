-- :name create-income!* :! :n
-- :doc creates a new income record
INSERT INTO income
(description, value, date)
VALUES (:description, :value, :date)

-- :name list-incomes :? :*
-- :doc retrieves a list of incomes
SELECT description, value, date
FROM income

-- :name list-description-incomes :? :*
-- :doc retrieves a list of incomes given a description
SELECT description, value, date
FROM income
WHERE description = :description

-- :name get-income :? :*
-- :doc retrieves a income given a income id
SELECT description, value, date
FROM income
WHERE id = :id

-- :name update-income!* :! :1
-- :doc updates a income record given a id
UPDATE income
SET description = :description, 
    value = :value, 
    date = :date
WHERE id = :id

-- :name delete-income!* :! :n
-- :doc deletes a income record given a id
DELETE FROM income
WHERE id = :id

-- :name list-duplicated-incomes :? :*
-- :doc retrieves a list of incomes with tha same description, month and year, but with a different id
SELECT * FROM (
    SELECT id, description, value, 
            EXTRACT(MONTH FROM date) AS month, 
            EXTRACT(YEAR FROM date) AS year
    FROM income) AS f
WHERE id <> :id AND description = :description AND
        month = :month AND year = :year
