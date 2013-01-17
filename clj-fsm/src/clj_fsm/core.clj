(ns clj-fsm.core)

(defmulti machine-property (fn [read-table property data] property))
(defmulti state-property (fn [read-table property data] property))

(defn add-state-data
  [table additional-state-data]
  {:pre [(map? additional-state-data)]}
  (swap! table #(merge-with merge % additional-state-data)))

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

(defn make-read-fn
  [a]
  (fn [] @a))

(defn fsm
  [machine-spec]
  (let [table (atom {})
        read-table (make-read-fn table)]
    (dorun (map
             (fn [[property data]]
               (add-state-data table (machine-property read-table property data)))
             machine-spec))
    (initial-state @table)))

(defmethod machine-property
  :initial
  [read-table _ initial-state]
  {initial-state {:initial true}})

(defn- read-state-data
  [read-table state-spec]
  (reduce
    merge {}
    (map
      (fn [[property data]]
        (state-property read-table property data))
      state-spec)))

(defmethod machine-property
  :states
  [read-table _ state-specs]
  (into {}
        (map
          (fn [[state-name state-spec]]
            [state-name (read-state-data read-table state-spec)])
          state-specs)))

(defmethod state-property
  :action
  [read-table _ action]
  {:action action})

(defmethod state-property
  :next-state
  [read-table _ next-state]
  {:transition (fn [& _] (get (read-table) next-state))})

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
  [read-table _ transitions]
  {:transition (fn [& args]
                 (get (read-table) (apply (next-state-fn transitions) args)))})

(defn act
  [state & args]
  {:pre [(:action state)]}
  (apply (state :action) args))

(defn transition
  [state & args]
  {:pre [(:transition state)]}
  (apply (state :transition) args))
