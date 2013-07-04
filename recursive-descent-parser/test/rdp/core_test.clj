(ns rdp.core-test
  (:use clojure.test
        rdp.core))

(deftest item-test
         (is (= [] (item "")))
         (is (= [[\a "bc"]] (item "abc"))))

(deftest char=-test
         (is (= [[\a "bc"]] ((char= \a) "abc")))
         (is (= [] ((char= \a) "xyz"))))

(deftest str=-test
         (is (= [["abc"" def"]] ((str= "abc") "abc def"))))

(deftest group-test
         (is (= [[["a" \b] "c"]]
                ((group
                   (str= "a") (char= \b))
                   "abc")))
         (is (= [[["a" \b] "c"]]
                ((group
                   "a" (char= \b))
                   "abc"))))

(deftest optional-test
         (let [parser (doparse
                        [first-bit (str= "a")
                         optional-bit (optional (str= "z"))
                         last-bit (str= "b")]
                        (str first-bit optional-bit last-bit))]
           (is (= [["ab" "c"]] (parser "abc")))
           (is (= [["azb" "c"]] (parser "azbc")))))

(deftest many-test
         (is (= [[[] "b"]] ((many (char= \a)) "b")))
         (is (= [[[\a \a \a] "b"]] ((many (char= \a)) "aaab"))))

(deftest many+-test
         (is (= [[[\a \a \a] "b"]] ((many+ (char= \a)) "aaab")))
         (is (= [] ((many+ (char= \a)) "b"))))
