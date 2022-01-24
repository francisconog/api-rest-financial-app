(ns fin-app.routes.service
  (:require [fin-app.db.core :as db]
            [fin-app.routes.income :refer [income-routes]]
            [fin-app.routes.outcome :refer [outcome-routes]]
            [clojure.java.io :as io]
            [ring.util.http-response :as response]
            [fin-app.middleware :as middleware]
            [clojure.pprint :refer [pprint]]))


(defn monthly-summary
  [{{year :year
     month :month} :path-params}]
  (let [monthly-summary (db/list-balance-summary {:year (Integer. year)
                                                  :month (Integer. month)})]
    (response/ok monthly-summary)))

(defn service-routes
  []
  ["/api" {:middleware [middleware/wrap-formats]}
   (income-routes)
   (outcome-routes)
   ["/summary"
    ["/:year/:month" {:get monthly-summary}]]])

