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
  ([description value date]
   (jdbc/with-transaction [t-conn db/*db*]
     (create-outcome! t-conn description value date)))
  ([t-conn description value date]
   (if (duplicate-outcome? t-conn -1 description date)
     (throw (ex-info "Duplicated outcome!"
                     {:fin-app/error-id ::duplicated-outcome
                      :error "Duplicated outcome!"}))
     (db/create-outcome!* t-conn {:description description
                                  :value value
                                  :date date}))))

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
   (if (empty? (db/get-outcome {:id (Integer. id)}))
     (throw (ex-info "outcome does not exists!"
                     {:fin-app/error-id ::not-exists
                      :error "outcome does not exists!"}))
     (let [outcome ((db/get-outcome {:id (Integer. id)}) 0)
           {current-description :description
            current-value :value
            current-date :date} outcome]
       [current-description current-value current-date])))

  ([t-conn id]
   (if (empty? (db/get-outcome t-conn {:id (Integer. id)}))
     (throw (ex-info "outcome does not exists!"
                     {:fin-app/error-id ::not-exists
                      :error "outcome does not exists!"}))
     (let [outcome ((db/get-outcome t-conn {:id (Integer. id)}) 0)
           {current-description :description
            current-value :value
            current-date :date} outcome]
       [current-description current-value current-date]))))

(defn get-new-outcomes
  "Asserts that a outcome feature stays the same if its value
  from the request is nil"
  [outcomes current-outcomes]
  (let [[description value date] outcomes
        [current-description current-value current-date] current-outcomes
        new-description (or description current-description)
        new-value (or value current-value)
        new-date (or (when (not (nil? date))
                       (ut/Parse-Date date))
                     current-date)]
    
    [new-description new-value new-date]))

(defn update-outcome!
  ([id description value date]
   (db/update-outcome!* {:description description
                         :value value
                         :date date
                         :id (Integer. id)}))
  ([t-conn id description value date]
   (db/update-outcome!* t-conn {:description description
                                :value value
                                :date date
                                :id (Integer. id)})))


(defn update-outcome-rule
  ([id new-outcomes]
   (let [id (Integer. id)
         [new-description new-value new-date] new-outcomes]
     (if (duplicate-outcome? id new-description new-date)
       (response/unauthorized
        {:message "Duplicated outcome, Please correct it!"})
       (do (update-outcome! id new-description
                            new-value new-date)
           (response/ok
            {:message "outcome updated with success"})))))
  ([t-conn id new-outcomes]
   (let [id (Integer. id)
         [new-description new-value new-date] new-outcomes]
     
     (if (duplicate-outcome? t-conn id new-description new-date)
       (response/unauthorized
        {:message "Duplicated outcome, Please correct it!"})
       (do (update-outcome! t-conn id new-description
                            new-value new-date)
           (response/ok
            {:message "outcome updated with success"}))))))
