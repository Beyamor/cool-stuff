(ns clj-fsm.core)

(defmulti machine-property (fn [read-table property data] property))
(defmulti state-property (fn [read-table property data] property))

(defn add-state-data
  "Adds state data (a map of state names to data)
   to the state table"
  [table additional-state-data]
  {:pre [(map? additional-state-data)]}
  (swap! table #(merge-with merge % additional-state-data)))

(defn initial-state
  "Gets the initial state from a state table.
   At least one of the states is expected to
   be marked as having :initial as true"
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
  "Makes a function which reads a value from an atom"
  [a]
  (fn [] @a))

(defn fsm
  "Creates a state machine from a machine spec,
   returning the initial state of the machine"
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

(defn- unify-maps
  "Merges a sequence of maps"
  [maps]
  (apply merge {} maps))

(defn- read-state-spec
  "Reads a state spec and generates
   state data based on its properties."
  [read-table state-spec]
  (unify-maps
    (map
      (fn [[property data]]
        (state-property read-table property data))
      state-spec)))

(defmethod machine-property
  :states
  [read-table _ state-specs]
  (unify-maps
    (map
      (fn [[state-name state-spec]]
        {state-name (read-state-spec read-table state-spec)})
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
  "Given a map of transitions
   (resulting state -> transition predicate),
   this returns a function which transitions
   to one of the specified states"
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
  "Calls the action function of the current state"
  [state & args]
  {:pre [(:action state)]}
  (apply (state :action) args))

(defn transition
  "Calls the transition function of the current state"
  [state & args]
  {:pre [(:transition state)]}
  (apply (state :transition) args))
