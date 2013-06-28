(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(def whitespace
  (many+ (is? #{\space \newline})))

(def number
  (group
    (optional (str= "-"))
    (many+
      (is? #{\1 \2 \3 \4 \5 \6 \7 \8 \9 \0}))))

(def ident
  (str= "x"))

(def block
  (group
    (optional-group
      "const" whitespace ident whitespace "=" whitespace number ";") 
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
         (is (= "var x;." (parse-program "var x;.")))
         (is (= "const x = -10;." (parse-program "const x = -10;."))))
