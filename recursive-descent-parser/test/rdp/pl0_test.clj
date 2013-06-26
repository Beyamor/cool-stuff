(ns rdp.pl0-test
  (:use clojure.test
        rdp.core))

(def ident
  (doparse
    [x (string= "x")]
    x))

(def block
  (doparse
    [s (group
         (string= "var ") ident (string= ";"))]
    s))

(def program
  (doparse
    [b block
     _ (char= \.)]
    (do
      (println "block is" b)
      (str b "."))))

(defn parse-program
  [program-text]
  (let [parse-results (program program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))

(comment
(deftest can-parse-mimimal-program
         (is (= "var x;." (parse-program "var x;.")))))
