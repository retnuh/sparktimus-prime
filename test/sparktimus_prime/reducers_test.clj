(ns sparktimus-prime.reducers-test
  (:require [clojure.test :refer :all]
            [sparktimus-prime.reducers :refer :all]
            [clojure.tools.logging :as log]
            [clojure.core.reducers :as r]
            ))


(deftest generate-multiples-test
  (testing "Generating multiples of a number"
    (is (= [3 6 9 12 15 18 21] (vec (generate-multiples 3 21))))
    (is (= [3 6 9 12 15 18 21] (vec (generate-multiples 3 21 false))))
    (is (= [3 9 15 21] (vec (generate-multiples 3 21 true))))
    ))

(deftest sieve-prime-multiples-test
  (testing "Getting a batch of new primes numbers where highest prime is < sqrt(N)"
    (let [numbers (vec (range 3 100 2))
          [new-primes remaining] (sieve-prime-multiples 100 [3 5 7] numbers)]
      (is (= [11 13 17 19 23 29 31 37 41 43 47] new-primes))
      (is (instance? java.util.ArrayList remaining))
      ;; Shouldn't contain factors
      (are [x] (is (not (.contains remaining x)))
        9 99 25 35 95 49 21 63 77 33)
      ;; Probably shouldn't contain the primes themselves!
      (are [x] (is (not (.contains remaining x)))
        11 13 17 19 23 29 31 37 41 43 47)
      )))

(deftest primes-upto-test
  (testing "Get the list of primes upto N"
    (let [prime-batches (primes-upto 75)]
      (are [batch-num primes] (is (= primes (nth prime-batches batch-num)))
        0 [2]
        1 [3]
        2 [5 7]
        3 [11 13 17 19 23 29 31 37 41 43 47]
        4 [53 59 61 67 71 73]))))
