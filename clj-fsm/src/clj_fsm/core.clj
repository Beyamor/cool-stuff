(ns clj-fsm.core)

(defn- get-state-action
  [{:keys [action]}]
  action)

(defn- get-transitions-fn
  [{:keys [transitions]}]
  {:pre [(not (nil? transitions))]}
  (fn [& args]
    {:post [(not (nil? %))]}
    (some
      (fn [[next-state transition-predicate]]
        (if (apply transition-predicate args) next-state))
      transitions)))

(defn- get-next-state-fn
  [state-name state-spec]
  (cond
    (contains? state-spec :next-state) (constantly (state-spec :next-state))
    (contains? state-spec :transitions) (get-transitions-fn state-spec)
    :else (constantly state-name)))

(defn- get-state-transitions
  [state-name state-spec state-table]
  (let [next-state-fn (get-next-state-fn state-name state-spec)]
    (fn [& args]
      {:post [(not (nil? %))]}
      (@state-table (apply next-state-fn args)))))

(defn fsm
  [spec]
  (let [state-table (atom {})
        state-specs (:states spec)
        initial-state-name (:initial spec)]
    (dorun
      (map
        (fn [[state-name state-spec]]
          (let [state-action (get-state-action state-spec)
                state-transitions (get-state-transitions state-name state-spec state-table)
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
