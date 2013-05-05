(ns linden.core
  (:use [clojure.string :only [split trim split-lines]]))

(defn remove-whitespace
  "Removes *all* whitespace from a string."
  [s]
  (clojure.string/replace (or s "") #"\s+" ""))

(defn string->word
  "Takes a string and turns into a word (a sequence of non-whitespace characters)"
  [s]
  (-> s remove-whitespace seq))

(def parse-axiom string->word)

(defn parse-rule
  "Parses a single rule of the form 'pred -> succ'"
  [input]
  (let [[pred succ] (split input #"->")]
    {:pred (-> pred string->word first)
     :succ (-> succ string->word)}))

(defn parse-rule-block
  "Reads a bunch of newline-separated rules and spits out a sequence of rules."
  [rule-block]
  (->>
    rule-block
    split-lines
    (filter #(re-find #".+->.+" %))
    (map parse-rule)))

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
