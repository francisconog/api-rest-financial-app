(ns fin-app.routes.service
  (:require [fin-app.routes.income :refer [income-routes]]
            [fin-app.routes.outcome :refer [outcome-routes]]
            [clojure.java.io :as io]
            [fin-app.middleware :as middleware]))



(defn service-routes
  []
  ["/api" {:middleware [middleware/wrap-formats]}
   (income-routes)
   (outcome-routes)])

