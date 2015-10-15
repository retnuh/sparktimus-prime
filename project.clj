(defproject sparktimus-prime "1.0.0-SNAPSHOT"
  :description "A small demonstration prime number sieve using Spark and Sparkling (Clojure Bindings for Spark)"
  :url "https://github.com/retnuh/sparktimus-prime"
  :license {:name "The UNLICENSE"
            :url  "http://unlicense.org"}

  :min-lein-version "2.0.0"

  :pom-addition [:developers
                 [:developer {:id "retnuh"}
                  [:name "Hunter Kelly"]
                  [:email "retnuh@gmail.com"]
                  [:role "Maintainer"]]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [gorillalabs/sparkling "1.2.3"]
                 [org.apache.spark/spark-mllib_2.10 "1.5.1"]
                 [org.apache.spark/spark-core_2.10 "1.5.1"]
                 ]
  :aot [clojure.tools.logging clojure.tools.logging.impl
        #".*"
        sparkling.serialization sparkling.destructuring sparkling.utils sparkling.core
        ]
  
  :jvm-opts ^:replace ["-server" "-Xmx2g"]
  :main sparktimus-prime.core
  :target-path "target/%s"
  :profiles {:dev {:repl-options {:init-ns user}
                   :source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [org.clojure/java.classpath "0.2.2"]]}
             :test {:aot [#".*-test"]}
             })
