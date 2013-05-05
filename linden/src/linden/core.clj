(ns linden.core
  (:use [clojure.string :only [split trim split-lines]]))

(defn parse-axiom
  [axiom]
  (->> axiom (map trim) (remove empty?)))

(defn parse-rule
  [input]
  (let [[pred succ] (split input #"->")]
    {:pred (trim pred)
     :succ (->> succ (map trim) (remove empty?))}))

(defn parse-rule-block
  "Reads a bunch of newline-separated rules and spits out a sequence of rules."
  [rule-block]
  (->> rule-block split-lines (remove empty?) (map parse-rule)))

(defn rule-book
  "Pieces together a collection of rules in a rulebook."
  [rules]
  (reduce
    (fn [book {:keys [succ pred]}]
      (if (contains? book pred)
        (update-in book [pred] conj succ)
        (assoc book pred #{succ})))
    {}
    rules))

(defn produce
  "Takes a successor and a rule book and produces something."
  [succ rule-book]
  (if (contains? rule-book succ)
    (-> (get rule-book succ) vec rand-nth)
    [succ]))

(defn transform
  "Transforms some input by producing its elements some number of times."
  [input number-of-iterations rule-book]
  (loop [input input, counter number-of-iterations]
    (if (> counter 0)
      (recur (mapcat #(produce % rule-book) input) (dec counter))
      input)))
