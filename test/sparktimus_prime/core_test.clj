(ns sparktimus-prime.core-test
  (:require [clojure.test :refer :all]
            [sparktimus-prime.core :refer :all]
            [clojure.tools.logging :as log]
            [sparkling.core :as spark]
            [sparkling.conf :as conf]
            [sparkling.destructuring :as s-de]
            [sparkling.serialization :as required-to-have-serializer-class-ready]
            )
  (:import [org.apache.spark.api.java JavaRDDLike]))


(defn make-test-context
  ([] (make-test-context (str *ns*)))
  ([test-name] 
   (-> (conf/spark-conf)
       (conf/master "local")
       (conf/app-name test-name)
       (conf/set "spark.driver.allowMultipleContexts" "true"))))

(deftest generate-multiples-test
  (testing "Generating multiples of a number"
    (is (= [3 6 9 12 15 18 21] (vec (generate-multiples 3 21))))
    (is (= [3 6 9 12 15 18 21] (vec (generate-multiples 3 21 false))))
    (is (= [3 9 15 21] (vec (generate-multiples 3 21 true))))
    ))

(deftest sieve-prime-multiples-test
  (spark/with-context ctx (make-test-context)
    (testing "Getting a batch of new primes numbers where highest prime is < sqrt(N)"
     (let [numbers-rdd (spark/parallelize ctx (range 3 100 2))
           [new-primes remaining-rdd] (sieve-prime-multiples ctx 100 [3 5 7] numbers-rdd)
           remaining (set (spark/collect remaining-rdd))]
       (is (= [11 13 17 19 23 29 31 37 41 43 47] new-primes))
       (is (instance? JavaRDDLike remaining-rdd))
       ;; Shouldn't contain factors
       (are [x] (is (not (contains? remaining x)))
         9 99 25 35 95 49 21 63 77 33)
       ;; Probably shouldn't contain the primes themselves!
       (are [x] (is (not (contains? remaining x)))
         11 13 17 19 23 29 31 37 41 43 47)
       ))
    ))

(deftest primes-upto-test
  (spark/with-context ctx (make-test-context)
    (testing "Get the list of primes upto N"
     (let [prime-batches (primes-upto ctx 75)]
       (are [batch-num primes] (is (= primes (nth prime-batches batch-num)))
         0 [2]
         1 [3]
         2 [5 7]
         3 [11 13 17 19 23 29 31 37 41 43 47]
         4 [53 59 61 67 71 73])))))
