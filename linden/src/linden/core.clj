(ns linden.core
  (:use [clojure.string :only [split trim]]
        seesaw.core
        [seesaw.color :only [color]]
        [seesaw.graphics :only [draw rect style]]))

(defn parse-rule
  [input]
  (let [[pred succ] (split input #"->")
        pred (trim pred)
        succ (->> succ (map trim) (remove empty?))]
    [pred succ]))

(defn draw-form
  [graphics instructions
   & {:keys [step-size angle-increment initial-x initial-y]
      :or {step-size 20 angle-increment 90 initial-x 300 initial-y 200}}]

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

(defn -main
  [& args]
  (invoke-later
    (->
      (frame
        :title "L-Systems"
        :content (vertical-panel
                   :items ["L-Systems"
                           (canvas
                             :size [600 :by 400]
                             :paint (fn [this graphics]
                                      (draw graphics
                                            (rect 0 0 600 400)
                                            (style :background :white))
                                      (draw-form
                                        graphics
                                        ["F" "F" "F" "-" "F" "F" "-" "F" "-" "F" "+" "F" "+" "F" "F" "-" "F" "-" "F" "F" "F"])))])
        :minimum-size [800 :by 600]
        :on-close :exit)
      pack!
      show!)))
