(ns api.place.resolvers
  (:require
   [api.place.model :as model]
   [api.place.db :as db]
   [api.util :as util]))

(def ^:private resolvers (atom {}))

(def defresolver
  (fn [name fn]
    (util/wrap-defresolver *ns* resolvers name fn)))

;; Resolvers
(defresolver :place-by-id
  (fn [{:keys [db]} {:keys [id]} _value]
    (db/select-place-by-id db id)))

(defresolver :place-by-name
  (fn [{:keys [db]} {:keys [name]} _value]
    (db/select-place-by-name db name)))

(defresolver :places
  (fn [{:keys [db]} _args _value]
    (db/select-places db)))

;; Mutations
(defresolver :create-place!
  (fn [{:keys [db]} {:keys [name]} _value]
    (let [new-place (model/->place name)
          result (db/insert-place db new-place)]
      (prn result)
      new-place)))

(defn ->resolvers [] @resolvers)
