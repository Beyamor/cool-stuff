(ns clj-fsm.test.core
  (:use [clj-fsm.core])
  (:use [clojure.test]))

(deftest can-set-machine-properties
         (let [table (atom {})
               _ (add-to-state table :state {:key :value})
               _ (machine-property table :initial :state)]
           (is (= {:key :value, :initial true} (initial-state @table))))) 

(deftest can-set-machine-states
         (let [table (atom {})
               _ (machine-property table :states {:state {:action :result}})]
           (is (= {:state {:action :result}} @table))))

(deftest can-use-state-actions
         (let [table (atom {})
               _ (machine-property table :states {:state {:action (constantly :result)}})
               state (:state @table)]
           (is (= :result (act state)))))

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
