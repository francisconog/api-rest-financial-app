CREATE OR REPLACE VIEW outcome_category AS 
    SELECT outcome.id AS id, description, value, 
           date, category_id, name AS category
    FROM outcome
    JOIN category
    ON outcome.category_id = category.id;