(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(defn block
  []
  (doparse
    [s (string= "var")]
    s))

(defn program
  []
  (doparse
    [b (block)
     _ (char= \.)]
    (str b ".")))

(defn parse-program
  [program-text]
  (let [parse-results ((program) program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))

(deftest can-parse-mimimal-program
         (is (= "var." (parse-program "var."))))
