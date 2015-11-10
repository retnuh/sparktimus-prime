(ns sparktimus-prime.reducers
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clojure.set :as s]
            [clojure.core.reducers :as r])
  (:gen-class))

(defn generate-multiples [x upto & [every-other?]]
  (let [incr (if every-other? (* 2 x) x)]
    (log/trace "gen-mult:" x upto every-other? incr)
    (take-while #(<= % upto) (iterate #(+ incr %) x))))

(defn sieve-prime-multiples [n primes numbers]
  (let [max-prime (last primes)
        upto (* max-prime max-prime)
        prime-multiples (->> primes
                             (r/mapcat #(generate-multiples % n (odd? %)))
                             (into #{}))
        candidates (->> numbers
                        (r/remove prime-multiples))
        new-primes (->> candidates
                        (r/filter #(< % upto))
                        r/foldcat
                        sort
                        (into []))
        remaining (->> candidates
                       (r/remove (set new-primes))
                       r/foldcat)]
    [new-primes remaining]))

(defn primes-upto
  "Find all the primes upto N.  Returns a seq of vectors of primes, containing the primes found in each pass."
  ([n] (primes-upto n [] [2] (vec (range 2 n))))
  ([n found-primes primes numbers]
   (let [highest (last primes)]
     (log/trace "pass:" found-primes primes (sort (seq numbers)))
     (if (>= (* highest highest) n)
       (conj found-primes primes (vec (sort (seq numbers))))
       (let [[new-primes remainder-numbers] (sieve-prime-multiples n primes numbers)]
         (log/trace "\t" new-primes (sort remainder-numbers))
         (recur n (conj found-primes primes) new-primes remainder-numbers))))))

(defn -main
  "Print out all the prime numbers up to passed in number.  DEBUG logger has the primes discovered
  in a given pass; the remaining primes from the final pass on their own line. Stdout is all the
  primes."
  [& args]
  (when-not (= 1 (count args))
    (println "Must specify exactly 1 argument - the N to calculate primes up to:" args)
    (System/exit -1))
  (let [prime-batches (primes-upto (Integer/parseInt (first args)))]
    (doseq [[i batch] (map-indexed vector (butlast prime-batches))]
      (log/debug "pass" (str  i ":") batch))
    (log/debug "remainder:" (last prime-batches))
    (apply println (flatten prime-batches))))
