(ns fin-app.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[fin-app started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[fin-app has shut down successfully]=-"))
   :middleware identity})
