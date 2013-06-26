(ns rdp.core-test
  (:use clojure.test
        rdp.core))

(deftest item-test
         (is (= [] (item "")))
         (is (= [[\a "bc"]] (item "abc"))))

(deftest char=-test
         (is (= [[\a "bc"]] ((char= \a) "abc")))
         (is (= [] ((char= \a) "xyz"))))

(deftest string=-test
         (is (= [["abc"" def"]] ((string= "abc") "abc def"))))

(deftest group-test
         (is (= [["ab" "c"]]
                ((group
                   (string= "a") (char= \b))
                   "abc"))))
