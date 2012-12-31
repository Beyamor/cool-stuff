(ns markovenizer.core)

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

(defn patterns
  "Generates the patterns from a given string.
   Patterns will be of max length pattern-length.
   e.g.,
    (patterns 2 'abc')
     ; => ['', a], ['a', b], ['ab', c]"
  [pattern-length text]
  (for [result-index (-> text count range)]
    (let [resulting-char (get text result-index)
          pattern (get-pattern text pattern-length result-index)]
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
