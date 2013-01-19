(ns clj-fsm.test.core
  (:use [clj-fsm.core])
  (:use [clojure.test]))

(deftest can-create-single-state-machine
  (let [fsm-state (fsm
                    {:initial :only-state
                     :states {:only-state {:action (constantly :result)
                                           :transitions {:only-state (constantly true)}}}})
        result (act fsm-state)]
    (is (= :result result))))

(deftest can-create-a-cyclic-state-machine
 (let [fsm-state (fsm
                   {:initial :a
                    :states {:a {:action (constantly :a)
                                 :transitions {:b (constantly true)}}
                             :b {:action (constantly :b)
                                 :transitions {:a (constantly true)}}}})
       res1 (act fsm-state)
       fsm-state (transition fsm-state)
       res2 (act fsm-state)
       fsm-state (transition fsm-state)
       res3 (act fsm-state)]
   (is (= [:a :b :a] [res1 res2 res3]))))

(deftest can-specify-next-state-directly
 (let [fsm-state (fsm
                   {:initial :a
                    :states {:a {:action (constantly :a)
                                 :next-state :b}
                             :b {:action (constantly :b)
                                 :next-state :a}}})
       res1 (act fsm-state)
       fsm-state (transition fsm-state)
       res2 (act fsm-state)
       fsm-state (transition fsm-state)
       res3 (act fsm-state)]
   (is (= [:a :b :a] [res1 res2 res3]))))

(deftest states-include-names
 (let [fsm-state (fsm
                   {:initial :a
                    :states {:a {:next-state :b}
                             :b {:next-state :a}}})
       name1 (state-name fsm-state)
       fsm-state (transition fsm-state)
       name2 (state-name fsm-state)]
   (is (= name1 :a))
   (is (= name2 :b))))

(deftest can-define-rule-of-state
 (let [cycle-state (fn [read-table {:keys [name] :as state}]
                     (assoc state
                            :transition
                            (always-state read-table (if (= :a name) :b :a))))
       fsm-state (fsm
                   {:initial :a
                    :states {:a {:next-state :b}
                             :b {:next-state :a}}
                    :rules {:of-state [cycle-state]}})
       name1 (state-name fsm-state)
       fsm-state (transition fsm-state)
       name2 (state-name fsm-state)
       fsm-state (transition fsm-state)
       name3 (state-name fsm-state)]
   (is (= [:a :b :a] [name1 name2 name3]))))
