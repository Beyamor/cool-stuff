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
