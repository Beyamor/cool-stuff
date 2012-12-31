(ns markovenizer.test.core
  (:use [markovenizer.core])
  (:use [clojure.test]))

(deftest can-generate-patterns
         (is (= [["" \a] ["a" :end]] (patterns 2 "a")))
         (is (= [["" \a] ["a" \b] ["ab" :end]] (patterns 2 "ab")))
         (is (= [["" \a] ["a" \b] ["ab" \c] ["bc" :end]] (patterns 2 "abc")))
         (is (= [["" \a] ["a" \b] ["ab" \c] ["bc" \d] ["cd" :end]] (patterns 2 "abcd")))
         (is (= [["" \a] ["a" \b] ["b" \c] ["c" \d] ["d" :end]] (patterns 1 "abcd")))
         (is (= [["" \a] ["a" \b] ["ab" \c] ["abc" \d] ["bcd" :end]] (patterns 3 "abcd"))))

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
         (is (= "abc" (build-string 1 {"" [\a] "a" [\b] "b" [\c]})))
         (is (= "abc" (build-string 1 {"" [\a] "a" [\b] "b" [\c] "c" [:end]}))))

(deftest can-build-model-from-lines
         (is (= {"" [\a \a] "a" [\b \b] "b" [:end \c] "c" [:end]}
                (build-model-from-lines 1 ["ab" "abc"]))))
