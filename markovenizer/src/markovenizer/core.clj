(ns markovenizer.core)

(defn patterns
  [pattern-length text]
  (for [result-index (-> text count range)]
    (let [resulting-char (get text result-index)
          pattern (.substring text
                              (max (- result-index pattern-length) 0)
                              result-index)]
      [pattern resulting-char])))
