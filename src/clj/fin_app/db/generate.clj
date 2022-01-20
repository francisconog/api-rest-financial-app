(ns fin-app.db.generate
  (:require [fin-app.db.core :as db]
            [clojure.java.io :as io]
            [next.jdbc :as jdbc]
            [fin-app.rules.income :as in-rules]
            [fin-app.rules.outcome :as out-rules]
            [fin-app.utils :as ut]
            [clojure.pprint :refer [pprint]]))

(def income-lists [["Salario" 2500 "01/01/2020"]
                   ["Salario" 2500 "01/02/2020"]
                   ["Salario" 2500 "01/03/2020"]])

(def outcome-lists [["Cartão de crédito" 1250 "01/01/2021"]
                    ["Cartão de crédito" 1000 "01/02/2021"]
                    ["Cartão de crédito" 2000 "01/03/2021"]])


(defn generate-income
  []
  (doseq [[description value date] income-lists]
    (in-rules/create-income!  description value (ut/Parse-Date date))))


(defn generate-outcome
  []
  (doseq [[description value date] outcome-lists]
    (out-rules/create-outcome!  description value (ut/Parse-Date date))))

;; (generate-income)
;; (generate-outcome)