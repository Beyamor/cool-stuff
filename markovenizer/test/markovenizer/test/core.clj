(ns markovenizer.test.core
  (:use [markovenizer.core])
  (:use [clojure.test]))

(deftest can-generate-patterns
         (is (= [[[] \a]] (patterns 2 "a"))))
