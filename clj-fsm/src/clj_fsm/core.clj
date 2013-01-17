(ns clj-fsm.core)

(defmulti machine-property (fn [table property data] property))
(defmulti state-property (fn [table property data] property))

(defn add-to-state
  [table state data]
  {:pre [(map? data)]}
  (swap! table update-in [state] merge data))

(defn initial-state
  [table]
  {:pre [(map? table)]
   :post [(map? %)]}
  (->>
    table
    (map (fn [[_ state-data]]
           (if (:initial state-data) state-data)))
    (filter identity)
    first))

(defn fsm
  [machine-spec]
  (let [table (atom {})]
    (dorun (map (fn [[property data]] (machine-property table property data)) machine-spec))
    (initial-state @table)))

(defmethod machine-property
  :initial
  [table _ initial-state]
  (add-to-state table initial-state {:initial true}))

(defn- state-data
  [table state-spec]
  (reduce
    merge {}
    (map
      (fn [[property data]]
        (state-property table property data))
      state-spec)))

(defmethod machine-property
  :states
  [table _ state-specs]
  (dorun
    (map
      (fn [[state state-spec]]
        (add-to-state
          table state (state-data table state-spec)))
      state-specs)))

(defmethod state-property
  :action
  [table _ action]
  {:action action})

(defmethod state-property
  :next-state
  [table _ next-state]
  {:transition (fn [& _] (@table next-state))})

(defn- next-state-fn
  [transitions]
  (fn [& args]
    {:post [%]}
     (some
       (fn [[next-state transition-predicate]]
         (if (apply transition-predicate args) next-state))
       transitions)))

(defmethod state-property
  :transitions
  [table _ transitions]
  {:transition (fn [& args]
                 (@table (apply (next-state-fn transitions) args)))})

(defn act
  [state & args]
  {:pre [(:action state)]}
  (apply (state :action) args))

(defn transition
  [state & args]
  {:pre [(:transition state)]}
  (apply (state :transition) args))
