(ns markovenizer.core)

(defn patterns
  "Generates the patterns from a given string.
   Patterns will be of max length pattern-length.
   e.g.,
    (patterns 2 'abc')
     ; => ['', a], ['a', b], ['ab', c]"
  [pattern-length text]
  (for [result-index (-> text count range)]
    (let [resulting-char (get text result-index)
          pattern (.substring text
                              (max (- result-index pattern-length) 0)
                              result-index)]
      [pattern resulting-char])))

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
