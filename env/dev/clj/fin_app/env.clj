(ns fin-app.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [fin-app.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[fin-app started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[fin-app has shut down successfully]=-"))
   :middleware wrap-dev})
