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
          "F"
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

(defn lsys-canvas
  [form]
  (canvas
     :id :canvas
     :size [600 :by 400]
     :paint (fn [this graphics]
              (draw graphics
                    (rect 0 0 600 400)
                    (style :background :white))
              (draw-form
                graphics
                @form))))

(defn labelled-el
  [label el]
  (horizontal-panel
    :items [(str label ": ") el]))

(defn with-border
  [el]
  (doto el (.setBorder (BorderFactory/createLineBorder Color/black))))

(defn generate-callback
  [root form]
  (fn [e]
    (let [axiom (-> (select root [:#axiom]) value parse-axiom)
          productions (-> (select root [:#productions]) value parse-rule-block)]
      (reset! form (transform axiom 4 (rule-book productions)))
      (repaint! (select root [:#canvas])))))

(defn -main
  [& args]
  (let [form (atom [])
        main (main-panel
               "L-Systems"
               (lsys-canvas form)
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
            :listen [:action (generate-callback main form)]))
    (invoke-later
      (-> (main-frame main) pack! show!))))
