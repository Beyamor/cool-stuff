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

(defn new-xel-set
  [& {:keys [width height xel-width xel-height colors]}]
  {:width width
   :height height
   :xels (into {}
               (for [i (range width)
                     j (range height)]
                 [[i j] (new-xel xel-width xel-height colors)]))})

(defn draw-xel!
  [canvas {xel-width :width xel-height :height :as xel}
   & {:keys [x y width height border]
      :or {border 0}}]
  (let [border2 (* border 2)
        cell-width (/ (- width border2) xel-width)
        cell-height (/ (- height border2) xel-height)]
    (doseq [i (range xel-width)
            j (range xel-height)]
      (doto canvas
        (cnvs/draw-rect!
          :x (+ x border (* i cell-width))
          :y (+ y border (* j cell-height))
          :width cell-width
          :height cell-height
          :color (get-in xel [:cells [i j]]))))))

(defn draw-xels!
  [canvas  {xels-width :width xels-height :height :as xels}
   & {:keys [x y width height border]
      :or {x 0 y 0 border 0}}]
  (let [xel-width (/ width xels-width)
        xel-height (/ height xels-height)]
    (doseq [i (range xels-width)
            j (range xels-height)]
      (doto canvas
        (draw-xel! (get-in xels [:xels [i j]])
                  :x (+ x (* i xel-width))
                  :y (+ y (* j xel-height))
                  :width xel-width
                  :height xel-height
                  :border border)))))

(defn run
  []
  (let [canvas (cnvs/create :width 600 :height 600
                            :clear-color "pink"
                            :parent "#app")
        xel-width 3
        xel-height 3
        xels (new-xel-set
               :width 3
               :height 3
               :xel-width 32
               :xel-height 32
               :colors #{"black" "white"})]
    (doto canvas
      cnvs/clear!
      (draw-xels! xels :width 600 :height 600 :border 10))))

($ run)
