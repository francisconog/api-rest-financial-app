(ns fin-app.routes.outcome
  (:require [fin-app.db.core :as db]
            [fin-app.rules.outcome :as out-rules]
            [fin-app.utils :as ut]
            [clojure.java.io :as io]
            [fin-app.middleware :as middleware]
            [clojure.pprint :refer [pprint]]
            [next.jdbc :as jdbc]
            [ring.util.response]
            [ring.util.http-response :as response]))


(defn get-outcome
  [request]
  (let [outcome-list (db/list-outcomes)]
    (-> (response/ok outcome-list))))

(defn add-new-outcome
  [{{:keys [description value date]} :params}]
  (let [parsed-date (ut/Parse-Date date)]
    (try
      (out-rules/create-outcome! description value parsed-date)
      (response/ok {:message "Outcome added with success"})
      (catch clojure.lang.ExceptionInfo e
        (response/unauthorized
         {:message "Duplicated outcome, Please correct it!"})))))

(defn detail-outcome
  [{{id :id} :path-params}]
  (try
    (let [outcome ((db/get-outcome {:id (Integer. id)}) 0)]
      (-> (response/ok outcome)))
    (catch java.lang.IndexOutOfBoundsException e
      (response/unauthorized
       {:message "Outcome does not exists!"}))
    (catch java.lang.NumberFormatException e
      (response/unauthorized
       {:message "Invalid outcome id"}))))

(defn edit-outcome
  [{{id :id} :path-params
    {:keys [description value date]} :params}];
  (jdbc/with-transaction [t-conn db/*db*]
    (let [current-outcomes (out-rules/get-current-outcomes t-conn id)
          new-outcomes (out-rules/get-new-outcomes [description value date]
                                                   current-outcomes)]
      (out-rules/update-outcome-rule t-conn id new-outcomes))))

(defn delete-outcome
  [{{id :id} :path-params}];
  (try
    (out-rules/delete-outcome! id)
    (response/ok
     {:message "Outcome deleted with success!"})
    (catch clojure.lang.ExceptionInfo e
      (response/unauthorized
       {:message "Outcome does not exists!"}))
    (catch java.lang.NumberFormatException e
      (response/unauthorized
       {:message "Invalid outcome id"}))))

(defn outcome-routes
  []
  ["/despesas"
   ["" {:get get-outcome
        :post add-new-outcome}]
   ["/:id" {:get detail-outcome
            :put edit-outcome
            :delete delete-outcome}]])