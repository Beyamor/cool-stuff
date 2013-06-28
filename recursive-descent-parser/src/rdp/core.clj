(ns rdp.core
  (:use [clojure.algo.monads :only [defmonad m-bind m-result domonad with-monad m-seq]]))

(defmonad parser-m
          [m-result (fn [a] ; takes a value
                      (fn [cs] ; and returns a parser
                        [[a cs]])) ; that returns the value and the chars unchanged

           m-bind (fn [parser mf] ; takes a parser and a function that returns a parser
                    (fn [cs] ; and returns a parser which
                      (apply concat
                             (for [[a cs'] (parser cs)] ; for each parse result
                               ((mf a) cs'))))) ; further parses with the produced parser

           m-zero (fn [cs] ; a parser that
                    []) ; always fails

           m-plus (fn [& parsers] ; given some parsers
                    (fn [cs] ; returns a parser which
                      (apply concat ; combines the parse results
                             (map #(% cs) parsers))))]) ; of all the given parsers
(defmacro doparse
  [bindings body]
  `(domonad parser-m
            ~bindings
            ~body))

(def pass
  (with-monad parser-m (m-result "")))

(defn first-of
  "Concatentates parsers, selects first result."
  [& parsers]
  (let [parser (with-monad parser-m
                           (apply m-plus parsers))]
    (fn [s]
      (let [results (parser s)]
        (if (empty? results)
          []
          [(first results)])))))

(def failed? empty?)

(defn str-rest
  "Returns rest of s as a string"
  [s]
  (subs s 1))

(defn item
  "A parser that consumes a single character or fails"
  [s]
  (if (empty? s)
    []
    [[(first s) (str-rest s)]]))

(defn is?
  "Returns a parser that either consumes a char meeting p? or fails"
  [p?]
  (doparse
    [c item ; grab the next item
     :when (p? c)] ; if it doesn't meet the predicate, return m-zero
    c)) ; otherwise, return c

(defn char=
  "Returns a parser that either consumes the given char or fails"
  [c]
  {:pre [(char? c)]}
  (is? #(= % c)))

(defn str=
  "Returns a parser that either consumes the given string or fails"
  [s]
  (if (empty? s) ; if we're looking for the empty string
    (with-monad parser-m
                (m-result "")) ; well, damn, we found it
    (doparse
      [_ (char= (first s)) ; if we can parse the first char
       _ (str= (str-rest s))] ; and the rest of the string
      s))) ; return the string

(defn group
  "Returns a parser that calls a sequence of parsers in series."
  [& parsers]
  (let [parsers (map #(if (string? %) (str= %) %) parsers)]
    (if (empty? parsers)
      pass
      (doparse
        [c (first parsers)
         cs (apply group (rest parsers))]
        (str c cs)))))

(defn optional
  "Returns an optional parser."
  [parser]
  (fn [s]
    (let [result (parser s)]
      (if (failed? result)
        ((with-monad parser-m (m-result "")) s)
        result))))

(defn optional-group
  [& parsers]
  (->> parsers (apply group) optional))

(declare many+)

(defn many
  "Repeats some parser 0 or more times"
  [parser]
  (domonad
    [cs (first-of (many+ parser) pass)]
    cs))

(defn many+
  "Repeats some parser 1 or more times"
  [parser]
  (doparse
    [c parser
     cs (first-of (many+ parser) pass)]
    (str c cs)))
