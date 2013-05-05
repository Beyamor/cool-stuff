(ns linden.core
  (:use [clojure.string :only [split trim split-lines]]))

(defn parse-rule
  [input]
  (let [[pred succ] (split input #"->")]
    {:pred (trim pred)
     :succ (->> succ (map trim) (remove empty?))}))

(defn parse-rule-block
  "Reads a bunch of newline-separated rules and spits out a sequence of rules."
  [rule-block]
  (->> rule-block split-lines (map parse-rule)))

(defn rule-book
  "Pieces together a collection of rules in a rulebook."
  [rules]
  (reduce
    (fn [book {:keys [succ pred]}]
      (if (contains? book succ)
        (update-in book [succ] conj pred)
        (assoc book succ #{pred})))
    {}
    rules))
