(ns api.env
  (:require
   [clojure.string :as st]))

(defn- get-env 
  ([env] (get-env env ""))
  ([env default] (or (System/getenv env) default)))

;; Postgres
(def pg-db-host (get-env "DB_HOST" "host.docker.internal"))
(def pg-db-name (get-env "DB_NAME" "db_api_catall"))
(def pg-db-user (get-env "DB_USER" "api_catall"))
(def pg-db-pass (get-env "DB_PASS" "api"))

;; Cassandra
(def ca-db-host (get-env "DB_HOST" "host.docker.internal:9042"))
(def ca-db-name (get-env "DB_NAME" "db_api_catall"))
(def ca-db-user (get-env "DB_USER" "api_catall"))
(def ca-db-pass (get-env "DB_PASS" "api"))
