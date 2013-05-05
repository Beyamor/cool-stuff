(ns linden.view
  (:use linden.core
        seesaw.core
        [seesaw.color :only [color]]
        [seesaw.graphics :only [draw rect style]])
  (:import javax.swing.BorderFactory
           java.awt.Color))

(defn draw-form
  [graphics instructions
   & {:keys [step-size angle-increment initial-x initial-y]
      :or {step-size 2 angle-increment 90 initial-x 300 initial-y 200}}]
  (.setColor graphics (color :black))
  (loop [x initial-x, y initial-y, angle 90, instructions instructions]
    (when (seq instructions)
      (let [[instruction & more-instructions] instructions]
        (case instruction
          "f"
          (let [next-x (+ x (-> angle Math/toRadians Math/cos (* step-size)))
                next-y (+ y (-> angle Math/toRadians Math/sin (* -1 step-size)))]
            (.drawLine graphics x y next-x next-y)
            (recur next-x next-y angle more-instructions))

          "-"
          (recur x y (- angle angle-increment) more-instructions)

          "+"
          (recur x y (+ angle angle-increment) more-instructions))))))

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

(defn paint-form
  [form & args]
  (fn [_ graphics]
    (draw graphics
          (rect 0 0 600 400)
          (style :background :white))
    (apply draw-form graphics form args)))

(defn lsys-canvas
  []
  (canvas
     :id :canvas
     :size [600 :by 400]))

(defn labelled-el
  [label el]
  (horizontal-panel
    :items [(str label ": ") el]))

(defn with-border
  [el]
  (doto el (.setBorder (BorderFactory/createLineBorder Color/black))))

(defn generate-callback
  [root]
  (fn [e]
    (let [axiom (-> (select root [:#axiom]) value parse-axiom)
          productions (-> (select root [:#productions]) value parse-rule-block)
          number-of-iterations (-> (select root [:#count]) value)
          step-size (-> (select root [:#step]) value)
          angle-increment (-> (select root [:#angle]) value)
          form (transform axiom number-of-iterations (rule-book productions))
          canvas (select root [:#canvas])]
      (config! canvas :paint (paint-form form :step-size step-size :angle-increment angle-increment))
      (repaint! canvas))))

(defn -main
  [& args]
  (let [main (main-panel
               "L-Systems"
               (lsys-canvas)
               (labelled-el "Number of iterations"
                            (slider :id :count :min 1 :max 6 :value 2 :major-tick-spacing 1 :snap-to-ticks? true))
               (labelled-el "Line length"
                            (slider :id :step :min 1 :max 30 :value 10))
               (labelled-el "Angle increment"
                            (slider :id :angle :min 15 :max 165 :value 90 :major-tick-spacing 5 :snap-to-ticks? true))
               (labelled-el "Axiom"
                            (->
                              (text :id :axiom :size [300 :by 20])
                              with-border))
               (labelled-el "Productions"
                            (->
                              (text :id :productions :multi-line? true :columns 20 :rows 8)
                              with-border
                              (scrollable :vscroll :always))))]
    (add! main
          (button
            :text "Give 'er"
            :listen [:action (generate-callback main)]))
    (invoke-later
      (-> (main-frame main) pack! show!))))
