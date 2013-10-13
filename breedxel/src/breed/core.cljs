(ns breed.core
  (:use [jayq.core :only [$]])
  (:require [breed.canvas :as cnvs]))

(defn new-xel
  [width height colors]
  {:width width
   :height height
   :cells (into {}
                 (for [x (range width)
                       y (range height)]
                   [[x y] (rand-nth colors)]))})

(defn draw-xel!
  [canvas {xel-width :width xel-height :height :as xel}
   & {:keys [x y width height]}]
  (let [cell-width (/ width xel-width)
        cell-height (/ height xel-height)]
    (doseq [i (range width)
            j (range height)]
      (doto canvas
        (cnvs/draw-rect!
          :x (+ x (* i cell-width))
          :y (+ y (* j cell-height))
          :width cell-width
          :height cell-height
          :color (get-in xel [:cells [i j]]))))))

(defn run
  []
  (let [canvas (cnvs/create :width 600 :height 600
                            :clear-color "black"
                            :parent "#app")
        xel (new-xel 32 32 #{"black" "white"})]
    (doto canvas
      cnvs/clear!
      (draw-xel! xel :x 0 :y 0 :height 600 :width 600))))

($ run)
