(ns api.server
  (:require
   [api.middleware :as middleware]
   [api.util :as util]
   [api.graphql :as graphql]
   [api.schema :as schema]
   [api.db :as db]
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.keyword-params :as keyword-params]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as log]))

(def app
  (ring/ring-handler
   (ring/router
    [["/graphql" {:post (graphql/handler (schema/full-schema) (db/datasource-with-options))}]]
    {:data {:muuntaja (m/create
                       (-> m/default-options
                           (assoc-in [:formats "application/json" :decoder-opts] {:decode-key-fn util/decode-keyword})
                           (assoc-in [:formats "application/json" :encoder-opts] {:encode-key-fn util/encode-keyword})))
            ;; A ordem importa.
            :middleware [middleware/exception
                         parameters/parameters-middleware
                         muuntaja/format-middleware
                         keyword-params/wrap-keyword-params]}})
   (ring/create-default-handler)
   {:middleware [middleware/logger
                 middleware/remove-slash]}))

(defn start [port]
  (let [server (jetty/run-jetty #'app {:port port, :join? false, :async? false})]
    (log/report (format "Server running at port %d. Jetty version: %s." port (org.eclipse.jetty.server.Server/getVersion)))
    server))

(defn stop [server]
  (.stop server)
  (log/report "Server stopped."))

(defn -main [port]
  (db/start)
  (start (Integer. port)))

(defn up [] (require 'api.server :reload-all))
