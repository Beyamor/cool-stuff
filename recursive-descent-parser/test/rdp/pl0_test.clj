(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(def whitespace
  (many+ (is? #{\space \newline})))

(def ident
  (str= "x"))

(def block
  (doparse
    [s (group
         (str= "var") whitespace ident (str= ";"))]
    s))

(def program
  (doparse
    [b block
     _ (char= \.)]
      (str b ".")))

(defn parse-program
  [program-text]
  (let [parse-results (program program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))

(deftest can-parse-mimimal-program
         (is (= "var x;." (parse-program "var x;."))))
