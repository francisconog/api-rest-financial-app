-- :name get-category_id :? :1
-- :doc retrieves the id of a category
SELECT id 
FROM category
WHERE name = :category
