(ns clj-fsm.core)

(defn- get-state-action
  [{:keys [action]}]
  action)

(defn- get-state-transitions
  [{:keys [transitions]} state-table]
  (fn [& args]
    (let [next-state-names (map
                             (fn [[next-state-name transition-predicate]]
                               (if (apply transition-predicate args)
                                 next-state-name))
                             transitions)
          next-state-name (->> next-state-names (filter identity) first)]
      (@state-table next-state-name))))

(defn fsm
  [spec]
  (let [state-table (atom {})
        state-specs (:states spec)
        initial-state-name (:initial spec)]
    (dorun
      (map
        (fn [[state-name state-spec]]
          (let [state-action (get-state-action state-spec)
                state-transitions (get-state-transitions state-spec state-table)
                state [state-action state-transitions]]
            (swap! state-table assoc state-name state)))
        state-specs))
    (@state-table initial-state-name)))

(defn act
  [[act-fn _] & args]
  (apply act-fn args))

(defn transition
  [[_ transition-fn] & args]
  (apply transition-fn args))
