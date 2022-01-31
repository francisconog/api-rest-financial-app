(ns fin-app.routes.income
  (:require [fin-app.db.core :as db]
            [fin-app.rules.income :as in-rules]
            [fin-app.utils :as ut]
            [clojure.java.io :as io]
            [fin-app.middleware :as middleware]
            [clojure.pprint :refer [pprint]]
            [next.jdbc :as jdbc]
            [ring.util.response]
            [ring.util.http-response :as response]))

(defn get-income
  [request]
  (if (-> request :query-params empty?)
    (let [income-list (db/list-incomes)]
      (response/ok income-list))
    (let [word (-> request :query-params (get "description"))
          income-list (db/list-incomes-with-word {:word word})]
      (response/ok income-list))))

(defn add-new-income
  [{{:keys [description value date]} :params}]
  (let [parsed-date (ut/Parse-Date date)]
    (try
      (in-rules/create-income! description value parsed-date)
      (response/ok {:message "Income added with success"})
      (catch clojure.lang.ExceptionInfo e
        (response/unauthorized
         {:message "Duplicated income, Please correct it!"})))))

(defn detail-income
  [{{id :id} :path-params}]
  (try
    (let [income ((db/get-income {:id (Integer. id)}) 0)]
      (-> (response/ok income)))
    (catch java.lang.IndexOutOfBoundsException e
      (response/unauthorized
       {:message "Income does not exists!"}))
    (catch java.lang.NumberFormatException e
      (response/unauthorized
       {:message "Invalid income id"}))))

(defn edit-income
  [{{id :id} :path-params
    {:keys [description value date]} :params}];
  (jdbc/with-transaction [t-conn db/*db*]
    (let [current-incomes (in-rules/get-current-incomes t-conn id)
          new-incomes (in-rules/get-new-incomes [description value date]
                                                current-incomes)]
      (in-rules/update-income-rule t-conn id new-incomes))))

(defn delete-income
  [{{id :id} :path-params}];
  (try
    (in-rules/delete-income! id)
    (response/ok
     {:message "Income deleted with success!"})
    (catch clojure.lang.ExceptionInfo e
      (response/unauthorized
       {:message "Income does not exists!"}))
    (catch java.lang.NumberFormatException e
      (response/unauthorized
       {:message "Invalid income id"}))))

(defn get-income-of-month
  [{{year :year
     month :month} :path-params}]
  (let [income-list (db/list-incomes-of-date {:year (Integer. year)
                                               :month (Integer. month)})]
    (response/ok income-list)))

(defn income-routes
  []
  ["/receitas" {:middleware [middleware/wrap-restricted]}
   ["" {:get get-income
        :post add-new-income}]
   ["/:id" {:get detail-income
            :put edit-income
            :delete delete-income}]
   ["/:year/:month" {:get get-income-of-month}]])