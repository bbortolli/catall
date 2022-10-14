(ns api.place.model
  (:require [api.util :as util]))

;; definição
;; (spec/def ::id util/uuid?)
;; (spec/def ::address address/address?)
;; (spec/def ::name string?)
;; (spec/def ::description string?)
;; (spec/def ::place (spec/keys :req-un [::id ::address ::name ::description]))

(defn ->place [name]
  (hash-map :id (util/uuid)
            :name name
            :modifiedat (util/->java-now-str)))
