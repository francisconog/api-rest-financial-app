(ns fin-app.auth
  (:require [buddy.hashers :as hashers]
            [next.jdbc :as jdbc]
            [buddy.auth :refer [authenticated?]]
            [fin-app.db.core :as db]))

(defn create-user!
  [login name email password & [admin]]

  (jdbc/with-transaction [t-conn db/*db*]
    (if-not (empty? (db/get-user-for-auth* t-conn {:login login}))
      (throw (ex-info "User already exists!"
                      {:fin-app/error-id ::duplicate-user
                       :error "User already exists!"}))
      (db/create-user!* t-conn {:login login
                                :name name
                                :email email
                                :password (hashers/derive password)
                                :admin (or admin false)}))))

(defn authenticate-user
  [login password]
  (let [{hashed :password :as user} (db/get-user-for-auth* {:login login})]
    (when (hashers/check password hashed)
      (dissoc user :password))))

(defn admin?
  [session]
  (-> session :identity :admin))