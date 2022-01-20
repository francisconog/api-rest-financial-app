-- :name create-outcome!* :! :n
-- :doc creates a new outcome record
INSERT INTO outcome
(description, value, date)
VALUES (:description, :value, :date)

-- :name list-outcomes :? :*
-- :doc retrieves a list of outcomes
SELECT description, value, date
FROM outcome

-- :name list-description-outcomes :? :*
-- :doc retrieves a list of outcomes given a description
SELECT description, value, date
FROM outcome
WHERE description = :description

-- :name get-outcome :? :*
-- :doc retrieves a outcome given a outcome id
SELECT description, value, date
FROM outcome
WHERE id = :id

-- :name update-outcome!* :! :1
-- :doc updates a outcome record given a id
UPDATE outcome
SET description = :description, 
    value = :value, 
    date = :date
WHERE id = :id

-- :name delete-outcome!* :! :n
-- :doc deletes a outcome record given a id
DELETE FROM outcome
WHERE id = :id

-- :name list-duplicated-outcomes :? :*
-- :doc retrieves a list of outcomes with tha same description, month and year, but with a different id
SELECT * FROM (
    SELECT id, description, value, 
            EXTRACT(MONTH FROM date) AS month, 
            EXTRACT(YEAR FROM date) AS year
    FROM outcome) AS f
WHERE id <> :id AND description = :description AND
        month = :month AND year = :year
