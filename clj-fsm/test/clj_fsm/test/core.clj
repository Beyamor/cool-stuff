(ns clj-fsm.test.core
  (:use [clj-fsm.core])
  (:use [clojure.test]))

(deftest can-create-single-state-machine
  (let [fsm (fsm
                    {:initial :only-state
                     :states {:only-state {:action (constantly :result)
                                           :transitions {:only-state (constantly true)}}}})
        result (act fsm)]
    (is (= :result result))))

(deftest can-create-a-cyclic-state-machine
 (let [fsm (fsm
                   {:initial :a
                    :states {:a {:action (constantly :a)
                                 :transitions {:b (constantly true)}}
                             :b {:action (constantly :b)
                                 :transitions {:a (constantly true)}}}})
       res1 (act fsm)
       fsm (transition fsm-state)
       res2 (act fsm)
       fsm (transition fsm-state)
       res3 (act fsm)]
   (is (= [:a :b :a] [res1 res2 res3]))))
