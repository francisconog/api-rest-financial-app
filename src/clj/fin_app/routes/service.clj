(ns fin-app.routes.service
  (:require [fin-app.db.core :as db]
            [fin-app.auth :as auth]
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

(defn login-handler
  [{{:keys [login password]} :params
    session :session}]
                ;;  (pprint {login password})
  (if-some [user (auth/authenticate-user login password)]
    (-> (response/ok "Success on login")
        (assoc :session (assoc session
                               :identity
                               user))
        (assoc :body {:identity user}))
    (response/unauthorized
     {:message "Incorrect login or password."})))

(defn logout-handler
  [_]
  (-> (response/ok "Success on logout")
      (assoc :session nil)))

(defn service-routes
  []

  ["/api" {:middleware [middleware/wrap-formats]}
   ["/login" {:handler login-handler}]
   ["/logout" {:handler logout-handler}]
   (income-routes)
   (outcome-routes)
   ["/summary" {:middleware [middleware/wrap-restricted]}
    ["/:year/:month" {:get monthly-summary}]]])

