-- :name create-user!* :! :n
-- :doc creates a new user record
INSERT INTO users
(login, name, email, password, admin)
VALUES (:login, :name, :email, :password, :admin)

-- :name update-user!* :! :n
-- :doc updates an existing user record
UPDATE users
SET name = :name, email = :email
WHERE id = :id

-- :name redefine-user-password!* :! :n
-- :doc updates an user password record
UPDATE users
SET password = :password
WHERE login = :login

-- :name get-user-for-auth* :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE login = :login

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id
