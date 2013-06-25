(ns monads.core-test
  (:use clojure.test
        monads.core))

(defmacro test-laws
  [monad a m f g]
  `(do
     ; left identity
     (is (= (~f ~a)
            (with-monad ~monad
                        (m-bind (m-result ~a) ~f))))

     ; right identity
     (is (= ~m
            (with-monad ~monad
                        (m-bind ~m m-result))))

     ; associativity
     (is (=
           (with-monad ~monad
                       (m-bind (m-bind ~m ~f) ~g))
           (with-monad ~monad
                       (m-bind ~m #(m-bind (~f %) ~g)))))))

(deftest monad-laws-hold-for-seq
         (let [f (fn [x] [(inc x)])
               g (fn [x] [(* 2 x)])]
           (test-laws sequence-m 1 [2] f g)))

(deftest lists-work-as-expected
         (let [f (fn [x] [(inc x)])]
           (with-monad sequence-m
                       (is (= [2 3 4] (m-bind [1 2 3] f))))))

(deftest monad-laws-hold-for-maybe
         (let [f (fn [x] (inc x))
               g (fn [x] (when (< x 10) (* 2 x)))]
           (test-laws maybe-m 1 1 f g)))

(deftest maybe-works-as-expected
         (with-monad maybe-m
                     (is (= 2 (m-bind 1 inc)))
                     (is (= nil (m-bind nil inc)))))

(deftest writer-works-as-expected
         (let [m-inc (fn [x] [(inc x) "inc."])
               m-double (fn [x] [(* 2 x) "double."])]
           (with-monad writer-m
                       (is (= [2 "inc."] (m-bind (m-result 1) m-inc)))
                       (is (= [4 "inc.double."] (m-bind (m-bind (m-result 1) m-inc) m-double))))))

(deftest monad-laws-hold-for-writer
         (let [f (fn [x] [(inc x) "inc"])
               g (fn [x] [(* 2 x) "double"])]
           (test-laws writer-m 1 [2 "nothing"] f g)))

(deftest can-do-seq
         (is (= [4 5 5 6]) 
             (domonad sequence-m
                      [x [1 2]
                       y [3 4]]
                      (+ x y))))

(deftest can-do-maybe
         (is (= nil
                (domonad maybe-m
                         [x 1
                          y nil]
                         (+ x y)))))
