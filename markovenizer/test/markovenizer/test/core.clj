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

(deftest can-amalgamate-patterns
         (is (= {"abc" [\d]}
                (build-model-from-patterns
                  [[["abc" \d]]])))
         (is (= {"abc" [\d \e]}
                (build-model-from-patterns
                  [[["abc" \d]] [["abc" \e]]])))
         (is (= {"abc" [\d \e] "xyz" [\w]}
                (build-model-from-patterns
                  [[["abc" \d] ["xyz" \w]] [["abc" \e]]]))))

(deftest can-get-result
         (is (= \d (result-for-pattern "abc" {"abc" [\d]})))
         (is (#{\d \e} (result-for-pattern "abc" {"abc" [\d \e]})))
         (is (= :end (result-for-pattern "xyz" {"abc" [\d \e]}))))

(deftest can-build-a-string
         (is (= "abc" (build-string 1 {"" [\a] "a" [\b] "b" [\c]}))))
