(ns clj-fsm.core)

(defmulti machine-property (fn [read-table property data] property))
(defmulti state-property (fn [read-table property data] property))

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

(defn- table-content*
  "Like table content, but only reads
   the machine properties in the spec."
  [read-table machine-spec]
  [read-table machine-spec]
    (apply merge-with merge
         (map (fn [[property data]]
                (machine-property read-table property data))
              machine-spec)))

(defn- table-content
  "Given a function to read table data,
   this returns the map resulting from
   handling all of the machine's properties.
   Does some post processing to inclued state names."
  [read-table machine-spec]
  (into {}
        (for [[state-name state-data] (table-content* read-table machine-spec)]
          [state-name (assoc state-data :name state-name)])))

(defn fsm
  "Creates a state machine from a machine spec,
   returning the initial state of the machine"
  [machine-spec]
  (let [table (atom {})
        read-table (make-read-fn table)]
    (reset! table (table-content read-table machine-spec))
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

(defn state-fn
  "Calls a function of the state"
  [attr state & args]
  {:pre [(get state attr)]}
  (apply (get state attr) args))

(defn- make-state-fn
  "Creates a function acting on a state"
  [attr]
  (partial state-fn attr))

(def act (make-state-fn :action))
(def transition (make-state-fn :transition))

(def state-name :name)
