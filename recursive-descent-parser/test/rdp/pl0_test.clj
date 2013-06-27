(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(def whitespace
  (many+ (is? #{\space \newline})))

(def ident
  (str= "x"))

(def block
  (group
    (str= "var") whitespace ident (str= ";")))

(def program
  (group
    block (char= \.)))

(defn parse-program
  [program-text]
  (let [parse-results (program program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))

(deftest can-parse-mimimal-program
         (is (= "var x;." (parse-program "var x;."))))
