(ns markovenizer.core
  (:use [clojure.java.io :only [reader]]))

(defn- get-pattern
  "Grabs a pattern from a string.
   Takes the length of the pattern and
   the (exclusive) end index of the pattern."
  ([text pattern-length end-index]
   (.substring text
               (max (- end-index pattern-length) 0)
               end-index))
  ([text pattern-length]
   (get-pattern text pattern-length (count text))))

(defn- char-patterns
  "Generates the patterns which result
   in a subsequent character."
  [pattern-length text]
  (for [result-index (-> text count range)]
    (let [resulting-char (get text result-index)
          pattern (get-pattern text pattern-length result-index)]
      [pattern resulting-char])))

(defn- end-pattern
  "Generates the pattern ending the text."
  [pattern-length text]
  [(get-pattern text pattern-length) :end])

(defn patterns
  "Generates the patterns from a given string.
   Patterns will be of max length pattern-length.
   e.g.,
    (patterns 2 'abc')
     ; => ['', a], ['a', b], ['ab', c], ['bc', :end]"
  [pattern-length text]
  (concat
    (char-patterns pattern-length text)
    [(end-pattern pattern-length text)]))

(defn build-model-from-patterns
  "Given a list of lists of pattern/results,
   this creates a model through which the result
   of a given pattern can be looked up."
  [all-patterns]
  (let [patterns (for [[pattern result] (->> all-patterns flatten (partition 2))]
                   {pattern [result]})]
    (reduce
      (partial merge-with into)
      patterns)))

(defn result-for-pattern
  "Given a pattern and a model,
   this grabs a random result for the pattern."
  [pattern model]
  (if-let [possible-results (model pattern)]
    (rand-nth possible-results)
    :end))

(defn build-string
  "Builds a string using the given model."
  [pattern-length model]
  (loop [s ""]
    (let [pattern (get-pattern s pattern-length)
          result (result-for-pattern pattern model)]
      (if (= :end result)
        s
        (recur (str s result))))))

(defn build-strings
  "Creates some number of strings using the given model."
  [number-to-generate pattern-length model]
  (take number-to-generate (repeatedly #(build-string pattern-length model))))

(defn build-new-strings
  "Creates some number of strings using the given model,
   none of which appeared in the original set of strings."
  [original-strings number-to-generate pattern-length model]
  (let [original-set (set original-strings)]
    (loop [new-strings []]
      (if (>= (count new-strings) number-to-generate)
        new-strings
        (let [new-string (build-string pattern-length model)]
          (if (original-set new-string)
            (recur new-strings)
            (recur (conj new-strings new-string))))))))

(defn build-model-from-lines
  "Builds a model from a bunch of text lines."
  [pattern-length lines]
  (build-model-from-patterns
    (map (partial patterns pattern-length) lines)))

(defn print-strings
  "Prints a bunch of strings generated from a model using the generator method."
  [generator number-to-print pattern-length model]
  (dorun
    (map-indexed
      (fn [index string] (println (str index ": " string)))
      (generator number-to-print pattern-length model))))

(defn -main
  [input-file & args]
  (let [pattern-length 3
        number-to-print 20
        lines (line-seq (reader input-file))
        model (build-model-from-lines pattern-length lines)
        generator (partial build-new-strings lines)]
    (print-strings generator number-to-print pattern-length model)))
