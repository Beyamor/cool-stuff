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

(deftest can-create-a-rule-book
         (is (= {"A" #{"B"}
                 "B" #{"A" "B"}}
                (rule-book
                  [{:pred "A" :succ "B"}
                   {:pred "B" :succ "A"}
                   {:pred "B" :succ "B"}]))))

(deftest can-produce
         (is (= "B" (produce "A" {"A" #{"B"}})))
         (is (= "A" (produce "A" {}))))
