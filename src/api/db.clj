(ns api.db
  (:require
   [api.env :as env]
   [next.jdbc :as jdbc]
   [camel-snake-kebab.core :as csk]
   [next.jdbc.result-set :as rs]
   [next.jdbc.date-time]
   [qbits.alia :as alia]
   [taoensso.timbre :as log]
   [clojure.string :as str]
   [clojure.pprint :as ppr]
   [qbits.alia.result-set :refer [RowGenerator]])
  (:import
   [com.datastax.oss.driver.api.core CqlIdentifier]
   [com.datastax.oss.driver.api.core.cql ColumnDefinition]))

;; Postgres
(def ^:private db-spec {:dbtype   "postgresql"
                        :dbname   env/pg-db-name
                        :host     env/pg-db-host
                        :user     env/pg-db-user
                        :password env/pg-db-pass})

(def opts {:column-fn csk/->snake_case :table-fn csk/->snake_case
           :label-fn csk/->kebab-case :qualifier-fn csk/->kebab-case
           :builder-fn rs/as-unqualified-kebab-maps})

(defn datasource-with-options []
  (jdbc/with-options (jdbc/get-datasource db-spec) opts))


;; Cassandra
;; Defs
(def db-cfg {:contact-points [env/ca-db-host] :session-keyspace "killrvideo" :load-balancing-local-datacenter "datacenter1"})
(def session (atom {}))
(def statements (atom {}))
(def prepared-statements (atom {}))

;; Manipulacao do db
(defn defcql [kw query]
  (swap! statements assoc kw query))

(defn create-session []
  (reset! session (alia/session db-cfg))
  (log/info "Session criada com sucesso"))

(defn prepare-assoc-statement [m kw stmt]
  (assoc m kw (alia/prepare @session stmt)))

(defn prepare-statements []
  (let [prepared-stmts (reduce-kv prepare-assoc-statement {} @statements)]
    (reset! prepared-statements prepared-stmts)
    (log/info "Statements preparados com sucesso")))

(def gen-row->map []
  (reify RowGenerator
    (init-row [_] (transient {}))
    (conj-row [_ row key v] 
      (let [^CqlIdentifier col-name (.getName ^ColumnDefinition key)
            internal-col-name (.asInternal col-name)
            kw (keyword (str/replace internal-col-name #"_" "-"))
            kw' (keyword (str/replace key #"_" "-"))]
        (assoc! row kw' v)))
    (finalize-row [_ row]  (persistent! row))))

(defn execute
  ([kw-pstmt]
   (execute kw-pstmt nil))
  ([kw-pstmt params]
  (when-let [pstmt (kw-pstmt @prepared-statements)]
    (alia/execute @session pstmt {:values params :row-generator gen-row->map}))))

(defn start []
  (create-session)
  (prepare-statements)
  (log/info "DB inicializado com sucesso"))

;; Queries
(defcql :select-all-videos-by-tag "SELECT * FROM videos_by_tag")
(defcql :select-videos-by-tag "SELECT * FROM videos_by_tag WHERE tag = :tag")

;; Funcoes
(defn select-videos-by-tag [tag]
  (let [rows (execute :select-videos-by-tag {:tag tag})
        rows' (map #(-> (update-in % [:added-date] str) (update-in [:video-id] str)) rows)]
    (ppr/print-table rows')))
