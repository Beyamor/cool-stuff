(ns linden.view
  (:use linden.core
        seesaw.core
        [seesaw.color :only [color]]
        [seesaw.graphics :only [draw rect style]])
  (:import javax.swing.BorderFactory
           java.awt.Color))

(defn step
  "Given a state, this adds a step to its x and y in its current direction."
  [{:as state :keys [angle]} step-size]
  (-> state
    (update-in [:x] + (-> angle Math/toRadians Math/cos (* step-size)))
    (update-in [:y] - (-> angle Math/toRadians Math/sin (* step-size)))))

(defn draw-form
  "Awesome. Draws a form, taking into acount some parameters."
  [graphics instructions
   & {:keys [step-size angle-increment initial-x initial-y]
      :or {step-size 2 angle-increment 90 initial-x 300 initial-y 200}}]
  (.setColor graphics (color :black))
  (loop [instructions instructions, state {:x initial-x :y initial-y :angle 90}]
    (when (seq instructions)
      (let [[instruction & more-instructions] instructions]
        (recur more-instructions
               (case instruction
                 \F
                 (let [next-state (step state step-size)]
                   (.drawLine graphics (:x state) (:y state) (:x next-state) (:y next-state))
                   next-state)

                 \f
                 (step state step-size)

                 \-
                 (update-in state [:angle] - angle-increment)

                 \+
                 (update-in state [:angle] + angle-increment)

                 ; In the default case, do nothing
                 state))))))



(defn main-panel
  [& contents]
    (vertical-panel
      :items contents))

(defn main-frame
  [contents]
  (frame
    :title "L-Systems"
    :content contents
    :minimum-size [800 :by 600]
    :on-close :exit))

(defn paint-model
  "Returns a function used to paint the given model."
  [model]
  (fn [el graphics]
    (let [{:keys [step-size angle-increment form]
           {:keys [x y]} :origin} @model]
      (draw graphics
            (rect 0 0 (width el) (height el))
            (style :background :white))
      (draw-form graphics form
                 :initial-x x
                 :initial-y y
                 :angle-increment angle-increment
                 :step-size step-size))))


(defn lsys-canvas
  "Creates a canvas which redraws when the model changes."
  [model]
  (let [c (canvas
            :size [600 :by 400]
            :paint (paint-model model))]
    (add-watch model nil
               (fn [_ _ _ model-value]
                 (repaint! c)))
    c))

(defn labelled
  "Stuffs the el in a panel alongside the given label."
  [el label]
  (horizontal-panel
    :items [(str label ": ") el]))

(defn with-border
  "Adds a black border to an el."
  [el]
  (doto el (.setBorder (BorderFactory/createLineBorder Color/black))))

(defn value-of
  "The value of some component, selected by id"
  [id root]
  (-> (select root [id]) value))

(defn on-change
  "Adds a listener which reacts to an element's new value."
  [el reaction]
  (listen el
          :change (fn [e] (-> (value el) reaction)))
  el)

(defn recalculate-form-when-any-change
  "When the given els change, the form is recalculated."
  [model root & els]
  (doseq [el els
          :let [change-types (if (instance? javax.swing.text.JTextComponent el)
                               #{:remove-update :insert-update}
                               :change)]]
    (listen el
            change-types
            (fn [_]
              (let [axiom (value-of :#axiom root)
                    productions (value-of :#productions root)
                    number-of-iterations (value-of :#count root)]
                (swap! model
                       assoc :form
                       (transform (parse-axiom axiom)
                                  number-of-iterations
                                  (-> productions parse-rule-block rule-book))))))))

(defn -main
  [& args]
  (let [model (atom {:form []
                     :step-size 10
                     :angle-increment 90
                     :origin {:x 300 :y 200}})

        main (main-panel
               "L-Systems"
               (lsys-canvas model)
               (->
                 (slider
                   :id :count
                   :min 1 :max 6 :value 2
                   :major-tick-spacing 1
                   :snap-to-ticks? true)
                 (labelled "Number of iterations"))
               (->
                 (slider
                   :id :step
                   :min 1 :max 30
                   :value (@model :step-size))
                 (on-change #(swap! model assoc :step-size %))
                 (labelled "Line length"))
               (->
                 (slider
                   :id :angle
                   :min 10 :max 170
                   :value (@model :angle-increment)
                   :major-tick-spacing 10
                   :snap-to-ticks? true)
                 (on-change #(swap! model assoc :angle-increment %))
                 (labelled "Angle increment"))
               (->
                 (text
                   :id :axiom
                   :size [300 :by 20])
                 with-border
                 (labelled "Axiom"))
               (->
                 (text
                   :id :productions
                   :multi-line? true
                   :columns 20
                   :rows 8)
                 with-border
                 (scrollable :vscroll :always)
                 (labelled "Productions")))]
    (recalculate-form-when-any-change
      model main
      (select main [:#count])
      (select main [:#axiom])
      (select main [:#productions]))
    (invoke-later
      (-> (main-frame main) pack! show!))))
