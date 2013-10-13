(ns breed.core
  (:use [jayq.core :only [$]])
  (:require [breed.canvas :as cnvs]))

(defn new-xel
  [columns rows colors]
  {:columns columns
   :rows rows
   :cells (into {}
                 (for [x (range columns)
                       y (range rows)]
                   [[x y] (rand-nth colors)]))})

(defn new-xel-set
  [& {:keys [columns rows xel-columns xel-rows colors]}]
  {:columns columns
   :rows rows
   :xels (into {}
               (for [i (range columns)
                     j (range rows)]
                 [[i j] (new-xel xel-columns xel-rows colors)]))})

(defn draw-xel!
  [canvas {:keys [columns rows] :as xel}
   & {:keys [x y width height border]
      :or {border 0}}]
  (let [border2 (* border 2)
        cell-width (/ (- width border2) columns)
        cell-height (/ (- height border2) rows)]
    (doseq [i (range columns)
            j (range rows)]
      (doto canvas
        (cnvs/draw-rect!
          :x (+ x border (* i cell-width))
          :y (+ y border (* j cell-height))
          :width cell-width
          :height cell-height
          :color (get-in xel [:cells [i j]]))))))

(defn draw-xels!
  [canvas  {:keys [columns rows] :as xels}
   & {:keys [x y width height border]
      :or {x 0 y 0 border 0}}]
  (let [xel-width (/ width columns)
        xel-height (/ height rows)]
    (doseq [i (range columns)
            j (range rows)]
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
               :columns 3
               :rows 3
               :xel-columns 32
               :xel-rows 32
               :colors #{"black" "white"})]
    (doto canvas
      cnvs/clear!
      (draw-xels! xels :width 600 :height 600 :border 10))))

($ run)
