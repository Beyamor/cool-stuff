(ns cljurtle.draw
  (:require [seesaw.color :refer [color]]))

(defn- draw-line!
  [graphics width height from to]
  (let [canvas-position       (fn [{:keys [x y]}]
                                {:x (+ x (/ width 2))
                                 :y (- height (+ y (/ height 2)))})
        {from-x :x from-y :y} (canvas-position from)
        {to-x :x to-y :y}     (canvas-position to)]
    (.drawLine graphics
               (int from-x) (int from-y)
               (int to-x)   (int to-y))))

(defn set-color!
  [graphics c]
  (.setColor graphics (color c)))

(defn turtle-sequence!
  [graphics width height states]
  (doseq [[current-state next-state] (partition 2 1 states)]
    (when (and (:pen-down? current-state)
               (not= (:position current-state)
                     (:position next-state)))
      (set-color! graphics (:pen-color current-state))
      (draw-line! graphics width height
                  (:position current-state)
                  (:position next-state)))))
