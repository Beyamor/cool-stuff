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
                                            (style :background :white))))])
        :minimum-size [800 :by 600]
        :on-close :exit)
      pack!
      show!)))
