(ns fin-app.rules.income
  (:require [fin-app.db.core :as db]
            [clojure.java.io :as io]
            [fin-app.utils :as ut]
            [next.jdbc :as jdbc]
            [ring.util.http-response :as response]
            [clojure.pprint :refer [pprint]]))

(defn duplicate-income?
  "Returns true if a income is duplicate, that is, if already exists
   a income with same description in a month."
  ([id description date]
   (let [month (ut/Get-Month date)
         year (ut/Get-Year date)
         incomes (db/list-duplicated-incomes {:id id :description description
                                              :month month :year year})]
     (not (empty? incomes))))
  ([t-conn id description date]
   (let [month (ut/Get-Month date)
         year (ut/Get-Year date)
         incomes (db/list-duplicated-incomes t-conn {:id id :description description
                                                     :month month :year year})]
     (not (empty? incomes)))))

(defn create-income!
  ([description value date]
   (jdbc/with-transaction [t-conn db/*db*]
     (create-income! t-conn description value date)))
  ([t-conn description value date]
   (if (duplicate-income? t-conn -1 description date)
     (throw (ex-info "Duplicated income!"
                     {:fin-app/error-id ::duplicated-income
                      :error "Duplicated income!"}))
     (db/create-income!* t-conn {:description description
                                 :value value
                                 :date date}))))

(defn delete-income!
  [id]
  (jdbc/with-transaction [t-conn db/*db*]

    (if (empty? (db/get-income t-conn {:id (Integer. id)}))
      (throw (ex-info "Income does not exists!"
                      {:fin-app/error-id ::not-exists
                       :error "Income does not exists!"}))
      (db/delete-income!* t-conn {:id (Integer. id)}))))


(defn get-current-incomes
  "Returns a list with the values of a income given a id"
  ([id]
   (if (empty? (db/get-income {:id (Integer. id)}))
     (throw (ex-info "Income does not exists!"
                     {:fin-app/error-id ::not-exists
                      :error "Income does not exists!"}))
     (let [income ((db/get-income {:id (Integer. id)}) 0)
           {current-description :description
            current-value :value
            current-date :date} income]
       [current-description current-value current-date])))

  ([t-conn id]
   (if (empty? (db/get-income t-conn {:id (Integer. id)}))
     (throw (ex-info "Income does not exists!"
                     {:fin-app/error-id ::not-exists
                      :error "Income does not exists!"}))
     (let [income ((db/get-income t-conn {:id (Integer. id)}) 0)
           {current-description :description
            current-value :value
            current-date :date} income]
       [current-description current-value current-date]))))

(defn get-new-incomes
  "Asserts that a income feature stays the same if its value
  from the request is nil"
  [incomes current-incomes]
  (let [[description value date] incomes
        [current-description current-value current-date] current-incomes
        new-description (or description current-description)
        new-value (or value current-value)
        new-date (or (when (not (nil? date))
                       (ut/Parse-Date date))
                     current-date)]
    [new-description new-value new-date]))

(defn update-income!
  ([id description value date]
   (db/update-income!* {:description description
                        :value value
                        :date date
                        :id (Integer. id)}))
  ([t-conn id description value date]
   (db/update-income!* t-conn {:description description
                               :value value
                               :date date
                               :id (Integer. id)})))


(defn update-income-rule
  ([id new-incomes]
   (let [id (Integer. id)
         [new-description new-value new-date] new-incomes]
     (if (duplicate-income? id new-description new-date)
       (response/unauthorized
        {:message "Duplicated income, Please correct it!"})
       (do (update-income! id new-description
                           new-value new-date)
           (response/ok
            {:message "Income updated with success"})))))
  ([t-conn id new-incomes]
   (let [id (Integer. id)
         [new-description new-value new-date] new-incomes]
     (if (duplicate-income? t-conn id new-description new-date)
       (response/unauthorized
        {:message "Duplicated income, Please correct it!"})
       (do (update-income! t-conn id new-description
                           new-value new-date)
           (response/ok
            {:message "Income updated with success"}))))))
