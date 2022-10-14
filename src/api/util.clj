(ns api.util
  (:require [jsonista.core :as json]
            [clojure.string :as st]
            [clojure.walk :as walk])
  (:import (clojure.lang IPersistentMap)))

(defn uuid [] (str (java.util.UUID/randomUUID)))

;; util.datetime
(defn new-timestamp []
  (let [gmt "+00:00"
        now (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
                     (java.util.Date.))]
    (str (subs now 0 26) gmt)))

;; util.json
(defn decode-keyword [s]
  (keyword (clojure.string/replace s \_ \-)))

(defn encode-keyword [kw]
  (clojure.string/replace (name kw) \- \_))

(def mapper-key-underscore
  (json/object-mapper
    {:decode-key-fn decode-keyword
     :encode-key-fn encode-keyword}))

(defn json->str [obj]
  (json/write-value-as-string obj mapper-key-underscore))

(defn str->json [s]
  (json/read-value s mapper-key-underscore))

;; util for resolvers
(defn- exception-resolver [ns kw]
  (format "Failed to define resolver: %s/%s." (keyword (str ns)) (name kw)))

(defn ->java-now-str [] (java.util.Date.))

(defn wrap-defresolver
  [ns context-atom name-keyword resolver-fn]
  (try
    (swap! context-atom assoc name-keyword resolver-fn)
    (catch Exception _
      (throw (ex-info (exception-resolver ns name-keyword) {:name name-keyword})))))

(defn simplify
  "Converts all ordered maps nested within the map into standard hash maps, and
   sequences into vectors, which makes for easier constants in the tests, and eliminates ordering problems."
  [m]
  (walk/postwalk
   (fn [node]
     (cond
       (instance? IPersistentMap node) (into {} node)
       (seq? node)                     (vec node)
       :else                           node))
   m))
