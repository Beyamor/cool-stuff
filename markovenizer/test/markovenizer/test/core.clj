(ns markovenizer.test.core
  (:use [markovenizer.core])
  (:use [clojure.test]))

(deftest can-generate-patterns
         (is (= [["" \a]] (patterns 2 "a")))
         (is (= [["" \a] ["a" \b]] (patterns 2 "ab")))
         (is (= [["" \a] ["a" \b] ["ab" \c]] (patterns 2 "abc")))
         (is (= [["" \a] ["a" \b] ["ab" \c] ["bc" \d]] (patterns 2 "abcd")))
         (is (= [["" \a] ["a" \b] ["b" \c] ["c" \d]] (patterns 1 "abcd")))
         (is (= [["" \a] ["a" \b] ["ab" \c] ["abc" \d]] (patterns 3 "abcd"))))
