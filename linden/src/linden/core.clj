(ns linden.core
  (:use [clojure.string :only [split trim]]))

(defn parse-rule
  [input]
  (let [[pred succ] (split input #"->")
        pred (trim pred)
        succ (->> succ (map trim) (remove empty?))]
    [pred succ]))
