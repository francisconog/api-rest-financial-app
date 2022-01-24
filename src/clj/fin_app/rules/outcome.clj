(ns fin-app.rules.outcome
  (:require [fin-app.db.core :as db]
            [clojure.java.io :as io]
            [fin-app.utils :as ut]
            [next.jdbc :as jdbc]
            [ring.util.http-response :as response]
            [clojure.pprint :refer [pprint]]))

(defn duplicate-outcome?
  "Returns true if a outcome is duplicate, that is, if already exists
   a outcome with same description in a month."
  ([id description date]
   (let [month (ut/Get-Month date)
         year (ut/Get-Year date)
         outcomes (db/list-duplicated-outcomes {:id id :description description
                                                :month month :year year})]
     (not (empty? outcomes))))
  ([t-conn id description date]
   (let [month (ut/Get-Month date)
         year (ut/Get-Year date)
         outcomes (db/list-duplicated-outcomes t-conn {:id id :description description
                                                       :month month :year year})]
     (not (empty? outcomes)))))

(defn create-outcome!
  ([description value date category_id]
   (jdbc/with-transaction [t-conn db/*db*]
     (create-outcome! t-conn description value date category_id)))
  ([t-conn description value date category_id]
   
   (if (duplicate-outcome? t-conn -1 description date)
     (throw (ex-info "Duplicated outcome!"
                     {:fin-app/error-id ::duplicated-outcome
                      :error "Duplicated outcome!"}))
     (db/create-outcome!* t-conn {:description description
                                  :value value
                                  :date date
                                  :category_id (or category_id 8)}))))

(defn delete-outcome!
  [id]
  (jdbc/with-transaction [t-conn db/*db*]

    (if (empty? (db/get-outcome t-conn {:id (Integer. id)}))
      (throw (ex-info "outcome does not exists!"
                      {:fin-app/error-id ::not-exists
                       :error "outcome does not exists!"}))
      (db/delete-outcome!* t-conn {:id (Integer. id)}))))

(defn get-current-outcomes
  "Returns a list with the values of a outcome given a id"
  ([id]
   (jdbc/with-transaction [t-conn db/*db*]
     (get-current-outcomes t-conn id)))
  ([t-conn id]
   (if (empty? (db/get-outcome t-conn {:id (Integer. id)}))
     (throw (ex-info "outcome does not exists!"
                     {:fin-app/error-id ::not-exists
                      :error "outcome does not exists!"}))
     (let [outcome ((db/get-outcome t-conn {:id (Integer. id)}) 0)
           {current-description :description
            current-value :value
            current-date :date
            current-category :category} outcome
           current-category_id (:id (db/get-category_id t-conn
                                                        {:category current-category}))]
       [current-description current-value current-date current-category_id]))))

(defn get-new-outcomes
  "Asserts that a outcome feature stays the same if its value
  from the request is nil"
  [outcomes current-outcomes]
  (let [[description value date category_id] outcomes
        [current-description current-value current-date current-category_id] current-outcomes
        new-description (or description current-description)
        new-value (or value current-value)
        new-date (or (when (not (nil? date))
                       (ut/Parse-Date date))
                     current-date)
        new-category_id (or category_id current-category_id)]

    [new-description new-value new-date new-category_id]))

(defn update-outcome!
  ([id description value date category_id]
   (jdbc/with-transaction [t-conn db/*db*]
     (update-outcome! [t-conn id description value date category_id])))
  ([t-conn id description value date category_id]
   (db/update-outcome!* t-conn {:description description
                                :value value
                                :date date
                                :id (Integer. id)
                                :category_id category_id})))

(defn update-outcome-rule
  ([id new-outcomes]
   (jdbc/with-transaction [t-conn db/*db*]
     (update-outcome-rule t-conn id new-outcomes)))
  ([t-conn id new-outcomes]
   (let [id (Integer. id)
         [new-description new-value new-date new-category_id] new-outcomes]

     (if (duplicate-outcome? t-conn id new-description new-date)
       (response/unauthorized
        {:message "Duplicated outcome, Please correct it!"})
       (do (update-outcome! t-conn id new-description
                            new-value new-date new-category_id)
           (response/ok
            {:message "outcome updated with success"}))))))


