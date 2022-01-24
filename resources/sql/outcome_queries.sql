-- :name create-outcome!* :! :n
-- :doc creates a new outcome record
INSERT INTO outcome
(description, value, date, category_id)
VALUES (:description, :value, :date, :category_id)

-- :name list-outcomes :? :*
-- :doc retrieves a list of outcomes
SELECT description, value, date, category
FROM outcome_category

-- :name list-description-outcomes :? :*
-- :doc retrieves a list of outcomes given a description
SELECT description, value, date, category
FROM outcome_category
WHERE description = :description

-- :name list-outcomes-of-category :? :*
-- :doc retrieves a list of outcomes given a category
SELECT description, value, date, category
FROM outcome_category
WHERE category = :category

-- :name get-outcome :? :*
-- :doc retrieves a outcome given a outcome id
SELECT description, value, date, category
FROM outcome_category
WHERE id = :id

-- :name update-outcome!* :! :1
-- :doc updates a outcome record given a id
UPDATE outcome
SET description = :description, 
    value = :value, 
    date = :date,
    category_id = :category_id
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

-- :name list-outcomes-of-date :? :*
-- :doc retrieves a list of outcomes within a given month
SELECT * FROM outcome
WHERE EXTRACT(MONTH FROM date) = :month AND 
      EXTRACT(YEAR FROM date) = :year;