(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(def whitespace
  (many+ (is? #{\space \newline})))

(def ident
  (str= "x"))

(def block
  (group
    (optional-group)
    (optional-group
      "var" whitespace ident ";")))

(def program
  (group
    block "."))

(defn parse-program
  [program-text]
  (let [parse-results (program program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))

(deftest can-parse-mimimal-program
         (is (= "var x;." (parse-program "var x;."))))
