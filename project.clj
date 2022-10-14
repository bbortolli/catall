(defproject api "api"
  :description "description"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.walmartlabs/lacinia "1.1"]
                 [camel-snake-kebab "0.4.2"]
                 [com.github.seancorfield/next.jdbc "1.2.796"]
                 [org.postgresql/postgresql "42.4.1"]
                 [camel-snake-kebab "0.4.2"]
                 [com.fasterxml.jackson.core/jackson-core "2.11.3"]
                 [com.fzakaria/slf4j-timbre "0.3.20"]
                 [com.taoensso/timbre "5.1.0"]
                 [metosin/jsonista "0.2.7"]
                 [metosin/reitit "0.5.10"]
                 [org.clojure/core.memoize "1.0.236"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [cc.qbits/alia "5.0.0"]
                 [cc.qbits/alia-async "5.0.0"]]
  :main ^:skip-aot api.server
  :repl-options {:init-ns api.server
                 :timeout 120000})
