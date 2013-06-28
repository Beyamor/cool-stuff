(ns rdp.pl0
  (:use rdp.core))

(def whitespace
  (many (is? #{\space \newline})))

(defn token
  [parser]
  (doparse
    [result parser
     _ whitespace]
    result))

(def number
  (token
    (group
      (optional (str= "-"))
      (many+
        (is? #{\1 \2 \3 \4 \5 \6 \7 \8 \9 \0})))))

(def ident
  (token
    (str= "x")))

(def defvar
  (doparse
    [_ (token (str= "var"))
     name ident
     _ (str= ";")]
    [:var name]))

(def defconst
  (doparse
    [_ (token (str= "const"))
     name ident
     _ (token (str= "="))
     value number
     _ (str= ";")]
    [:const name value]))

(def block
  (doparse
    [var-def (optional defvar)
     const-def (optional defconst)]
    [:block
     (filter identity [var-def const-def])]))

(def program
  (doparse
    [program block
     _ (str= ".")]
    [:program program]))

(defn parse-program
  [program-text]
  (let [parse-results (program program-text)]
    (if-not (empty? parse-results)
      (ffirst parse-results)
      (throw (Exception. "Syntax error in program")))))
