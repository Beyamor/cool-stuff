(ns cljurtle.draw
  (:require [cljurtle.core :as core]))

(defn- draw-line!
  [{:keys [width height context]} from to]
  (let [canvas-position       (fn [{:keys [x y]}]
                                {:x (+ x (/ width 2))
                                 :y (- height (+ y (/ height 2)))})
        {from-x :x from-y :y} (canvas-position from)
        {to-x :x to-y :y}     (canvas-position to)]
    (doto context
      .beginPath
      (.moveTo from-x from-y)
      (.lineTo to-x to-y)
      .stroke)))

(defn- set-color!
  [{:keys [context]} color]
  (set! (.-strokeStyle context) color))

(defn turtle!
  [canvas turtle]
  (let [states (core/state-sequence turtle)]
    (loop [[current-state & more-states] states]
      (when (seq more-states)
        (let [next-state (first more-states)]
          (when (and (:pen-down? current-state)
                     (not= (:position current-state)
                           (:position next-state)))
            (set-color! canvas (:pen-color current-state))
            (draw-line! canvas
                        (:position current-state)
                        (:position next-state)))
          (recur more-states))))))
