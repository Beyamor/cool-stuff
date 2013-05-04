(ns linden.core-test
  (:use clojure.test
        linden.core))

(deftest can-parse-rules
         (are [input output] (= (parse-rule input) output)
              "F -> F-F+FF" ["F" ["F" "-" "F" "+" "F" "F"]]
              "F  -> F - F  +F F" ["F" ["F" "-" "F" "+" "F" "F"]]))
