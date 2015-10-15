(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.test :as test]   
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all disable-unload!]]
   [clojure.string :as string]
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [sparkling.conf :as conf]
   [sparkling.core :as spark]
   [sparkling.utils :as spark-utils :refer :all]
   [sparkling.destructuring :as s-de])
  )

;; Stop the reloading magic from unloading the user namespace, stop
;; irritation when compile error, etc.
(disable-unload!)

(defn recompile
  ([] (recompile (symbol (str *ns*))))
  ([the-ns & other-nss] (when the-ns
                          (binding [*compile-files* true] (require the-ns :reload-all :verbose) the-ns)
                          (recur (first other-nss) (rest other-nss)))))
