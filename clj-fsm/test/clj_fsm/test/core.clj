(ns clj-fsm.test.core
  (:use [clj-fsm.core])
  (:use [clojure.test]))

(deftest can-create-single-state-machine
  (let [fsm-state (fsm
                    {:initial :only-state
                     :states {:only-state {:action (constantly :result)
                                           :transitions {:only-state [(constantly true)]}}}})
        result (act fsm-state)]
    (is (= :result result))))
