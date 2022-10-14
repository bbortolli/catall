(ns api.graphql
  (:require
   [api.util :as util]   
   [taoensso.timbre :as log]
   [com.walmartlabs.lacinia :as lacinia]
   [clojure.string :as string]
   [clojure.core.memoize :as memo]))

(def cache-in-minutes 1)

(def with-cache-resolver
  (memo/ttl
   (fn [compiled-schema query vars context]
     (-> compiled-schema
         (lacinia/execute query vars context)
         util/simplify))
   :ttl/threshold (* cache-in-minutes 60000)))

;; We do a little bit more error handling here in the case
;; where the client gives us non-valid JSON. We still haven't
;; handed over the values of the request object to lacinia
;; GraphQL so we are still responsible for minimal error
;; handling
(defn- get-variables [{:keys [request-method] :as request}]
  (try
    (case request-method
      :get (get-in request [:query-params "variables"])
      :post (get-in request [:body-params :variables])
      :else {})
    (catch Exception e
      (log/warn (format "Exception trying to parse variables from %s: %s" request-method e)))))

(defn variable-map
  "Reads the `variables` query parameter, which contains a JSON string
  for any and all GraphQL variables to be associated with this request.
  Returns a map of the variables (using keyword keys)."
  [request]
  (let [variables (get-variables request)]
    (if (seq variables)
      variables
      {})))

(defn extract-query
  "Reads the `query` query parameters, which contains a JSON string
  for the GraphQL query associated with this request. Returns a
  string.  Note that this differs from the PersistentArrayMap returned
  by variable-map. e.g. The variable map is a hashmap whereas the
  query is still a plain string."
  [{:keys [request-method] :as request}]
  (try
    (case request-method
      :get (get-in request [:query-params "query"])
      :post (get-in request [:body-params :query])
      :else "")
    (catch Exception e
      (log/warn (format "Exception trying to parse query from %s: %s" request-method e)))))

(defn extract-authorization-key
  "Extract the authorization key from the request header. The
  authorization header is of the form: Authorization: bearer <key>"
  [request]
  (when-let [auth-header (-> request :headers (get "Authorization"))]
    (last (string/split auth-header #"\s"))))

(defn handler
  "Accepts a GraphQL query via GET or POST, and executes the query.
  Returns the result as text/json."
  [compiled-schema db]
  (let [context {:cache (atom {}) :db db}]
    (fn [request]
      ;; include authorization key in context
      (swap! (:cache context) assoc :authorization (extract-authorization-key request))
      (let [vars (variable-map request)
            query (extract-query request)
            result (with-cache-resolver compiled-schema query vars context)
            status (if (-> result :errors seq) 400 200)]
        {:status status
         :headers {"Content-Type" "application/json"}
         :body (util/json->str result)}))))
