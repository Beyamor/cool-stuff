(ns rdp.pl0-test
  (:use clojure.test
        rdp.pl0))

(deftest can-parse-mimimal-program
         (is (= [:program [:block [[:var "x"]]]] (parse-program "var x;.")))
         (is (= [:program [:block [[:const "x" "-10"]]]] (parse-program "const x = -10;."))))
