(ns api.place.db
  (:require  
   [next.jdbc.sql :as sql]))

;; DB operations
(defn select-places [db]
  (sql/find-by-keys db :place {}))

(defn select-place-by-id [db id]
  (sql/get-by-id db :place id))

(defn select-place-by-name [db name]
  (sql/find-by-keys db :place {:name name}))

(defn insert-place [db place]
  (sql/insert! db :place place))
