(ns api.schema
  (:require
   [api.place.resolvers :as place-resolvers]
   [clojure.java.io :refer [resource]]
   [clojure.edn :as edn]
   [com.walmartlabs.lacinia.schema :as schema]
   [com.walmartlabs.lacinia.util :refer [attach-resolvers]]))

(defn ->all-resolvers []
  (merge {}
   (place-resolvers/->resolvers)))

(defn full-schema
  []
  (-> (resource "edn/schema.edn")
      slurp
      edn/read-string
      (attach-resolvers (->all-resolvers))
      schema/compile))
