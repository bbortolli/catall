(ns api.middleware
  (:require
   [taoensso.timbre :as log]
   [clojure.string :as string]))

(def exception
  {:name ::exception
   :wrap (fn [handler]
           (fn [req]
             (try
               (handler req)
               (catch Exception e
                 (hash-map :status 500
                           :msg "Não foi possível executar a requisição."
                           :exception e)))))})

(def logger
  {:name ::logger
   :wrap (fn [handler]
           (fn [req]
             (let [antes (System/currentTimeMillis)
                   resp (handler req)
                   info (merge (select-keys req [:request-method :protocol :uri :remote-addr])
                               (select-keys resp [:status])
                               {:time-ms (- (System/currentTimeMillis) antes)})]
               (if (< (:status resp) 500)
                 (if (seq (:warn resp))
                   (log/warn (:warn resp) info)
                   (log/info info))
                 (log/error (:exception resp) info))
               resp)))})

(def remove-slash
  {:name ::remove-slash
   :wrap (fn [handler]
           (fn [req]
             (if (string/ends-with? (:uri req "") "/")
               (update-in req [:uri] string/replace-first #"/+$" "")              
               (handler req))))})
