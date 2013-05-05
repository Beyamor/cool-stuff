(ns linden.core-test
  (:use clojure.test
        linden.core))

(deftest can-parse-rules
         (are [input output] (= (parse-rule input) output)
              "F -> F-F+FF" {:pred "F"
                             :succ ["F" "-" "F" "+" "F" "F"]}
              "F  -> F - F  +F F" {:pred "F"
                                   :succ ["F" "-" "F" "+" "F" "F"]}))

(deftest can-parse-rule-blocks
         (is (= [{:pred "F"
                  :succ ["F" "-" "F"]}
                 {:pred "F"
                  :succ ["F" "F" "-" "F"]}]
                (parse-rule-block
                  "F -> F-F
                   F -> FF-F"))))
