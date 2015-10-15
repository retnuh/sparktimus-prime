(ns sparktimus-prime.core
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [sparkling.conf :as conf]
            [sparkling.core :as spark]
            [sparkling.destructuring :as s-de])
  (:gen-class))

(defn make-spark-context [& {:as opt-args}]
  (-> (conf/spark-conf)
      (conf/master "local[*,4]")
      (conf/app-name "Sparktimus Prime")))

(defn generate-multiples [x upto & [every-other?]]
  (let [incr (if every-other? (* 2 x) x)]
  (log/trace "gen-mult:" x upto every-other? incr)
    (take-while #(<= % upto) (iterate #(+ incr %) x))))

(defn sieve-prime-multiples [ctx n primes numbers-rdd]
  (let [max-prime (last primes)
        upto (* max-prime max-prime)
        prime-multiples-rdd (->> (spark/parallelize ctx primes)
                             (spark/flat-map #(generate-multiples % n (odd? %))))
        candidates-rdd (spark/cache (.subtract numbers-rdd prime-multiples-rdd))
        new-primes-rdd (->> candidates-rdd
                            (spark/filter #(< % upto))
                            spark/cache)
        new-primes (vec (sort (spark/collect new-primes-rdd)))
        remaining-rdd (.subtract candidates-rdd new-primes-rdd)]
    (.unpersist candidates-rdd false)
    (.unpersist new-primes-rdd false)
    [new-primes remaining-rdd]))

(defn primes-upto
  "Find all the primes upto N.  Returns a seq of vectors of primes, containing the primes found in each pass."
  ([ctx n] (primes-upto ctx n [] [2] (spark/parallelize ctx (range 2 n))))
  ([ctx n found-primes primes numbers-rdd]
   (let [highest (last primes)]
         (log/trace "pass:" found-primes primes (sort (spark/collect numbers-rdd)))
     (if (>= (* highest highest) n)
       (conj found-primes primes (vec (sort (spark/collect numbers-rdd))))
       (let [[new-primes remainder-numbers] (sieve-prime-multiples ctx n primes numbers-rdd)]
         (log/trace "\t" new-primes (sort (spark/collect remainder-numbers)))
         (recur ctx n (conj found-primes primes) new-primes remainder-numbers))))))

(defn -main
  "Print out all the prime numbers up to passed in number.  DEBUG logger has the primes discovered in a given pass; 
the remaining primes from the final pass on their own line. Stdout is all the primes."
  [& args]
  (when-not (= 1 (count args))
    (println "Must specify exactly 1 argument - the N to calculate primes up to.")
    (System/exit -1))
  (let [prime-batches (spark/with-context ctx (make-spark-context) (primes-upto ctx (Integer/parseInt (first args))))]
    (doseq [[i batch] (map-indexed vector (butlast prime-batches))]
      (log/debug "pass" (str  i ":") batch))
    (log/debug "remainder:" (last prime-batches))
    (apply println (flatten prime-batches))))
