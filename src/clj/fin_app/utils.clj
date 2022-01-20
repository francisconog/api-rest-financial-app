(ns fin-app.utils)

(def sdf (java.text.SimpleDateFormat. "dd/MM/yyyy"))
(defn parse-date
  [date]
  (.parse sdf date))

(defn Parse-Date
  [date]
  (if (nil? date)
    (java.util.Date.)
    (parse-date date)))

(defn get-year
  [date]
  (+ 1900 (.getYear date)))

(defn Get-Year
  [date]
  (cond
    (= (type date) java.util.Date) (get-year date)
    (= (type date) java.time.LocalDateTime) (.getYear date)
    :else nil))

(defn get-month
  [date]
  (+ 1 (.getMonth date)))

(defn Get-Month
  [date]
  (cond
    (= (type date) java.util.Date) (get-month date)
    (= (type date) java.time.LocalDateTime) (.getMonthValue date)
    :else nil))

(defn DateTime-to-Year
  [DateTime]
  (-> DateTime
      :date
      (.getYear)))

(defn DateTime-to-Month
  [DateTime]
  (-> DateTime
      :date
      (.getMonthValue)))