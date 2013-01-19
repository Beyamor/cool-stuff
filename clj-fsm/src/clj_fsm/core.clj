(ns clj-fsm.core)

(defmulti machine-property (fn [read-table property data] property))
(defmulti state-property (fn [read-table property data] property))

(def unify-maps (partial apply merge {}))

(defn initial-state
  "Gets the initial state from a state table.
   At least one of the states is expected to
   be marked as having :initial as true"
  [table]
  {:pre [(map? table)]
   :post [(map? %)]}
  (first
    (for [[_ state-data] table
          :when (:initial state-data)]
      state-data)))

(defn make-read-fn
  "Makes a function which reads a value from an atom"
  [a]
  (fn [] @a))

(defn- machine-content*
  "Like machine-content, but only reads
   the machine properties in the spec."
  [read-table machine-spec]
    (apply merge-with (partial merge-with merge)
           (for [[property data] machine-spec]
             (machine-property read-table property data))))

(defn- add-state-names
  "Given a map of states,
   this associates each state's name
   with its data"
  [states]
  (unify-maps
         (for [[state-name data] states]
           {state-name (assoc data :name state-name)})))

(defn- machine-content
  "Given a function to read table data,
   this returns the map resulting from
   handling all of the machine's properties.
   Does some post processing to inclued state names."
  [read-table machine-spec]
  (unify-maps
        (for [[property data] (machine-content* read-table machine-spec)]
          (if (= :states property)
            {property (add-state-names data)}
            {property data}))))

(defn- apply-rules-of-state
  "Applies rules of state to a state map"
  [rules-of-state read-table states]
  (reduce
    (fn [states rule-of-state]
      (unify-maps
        (for [[state-name data] states]
          {state-name (rule-of-state read-table data)})))
    states
    rules-of-state))

(defn- apply-rules
  "Given a map with rules and states,
   this applies the rules to the machine"
  [read-table {:keys [states] {state-rules :of-state} :rules}]
  (let [states (if state-rules (apply-rules-of-state state-rules read-table states) states)]
    states))

(defn fsm
  "Creates a state machine from a machine spec,
   returning the initial state of the machine"
  [machine-spec]
  (let [table (atom {})
        read-table (make-read-fn table)
        content (machine-content read-table machine-spec)]
    (reset! table (apply-rules read-table content))
    (initial-state @table)))

(defmethod machine-property
  :initial
  [read-table _ initial-state]
  {:states {initial-state {:initial true}}})

(defn- read-state-spec
  "Reads a state spec and generates
   state data based on its properties."
  [read-table state-spec]
  (unify-maps
    (for [[property data] state-spec]
      (state-property read-table property data))))

(defmethod machine-property
  :states
  [read-table _ state-specs]
  {:states
   (unify-maps
    (for [[state-name state-spec] state-specs]
      {state-name (read-state-spec read-table state-spec)}))})

(defmethod state-property
  :action
  [read-table _ action]
  {:action action})

(defn always-state
  "Always returns the same state"
  [read-table next-state]
  (fn [& _]
    (get (read-table) next-state)))

(defmethod state-property
  :next-state
  [read-table _ next-state]
  {:transition (always-state read-table next-state)})

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
                 (get (read-table)
                      (apply (next-state-fn transitions) args)))})

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

(defmethod machine-property
  :rules
  [read-table _ rule-sets]
  {:rules rule-sets})
