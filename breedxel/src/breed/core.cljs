(ns breed.core
  (:use [jayq.core :only [$]])
  (:require [breed.canvas :as cnvs]))

(defn new-xel
  [width height colors]
  {:width width
   :height height
   :pixels (into {}
                 (for [x (range width)
                       y (range height)]
                   [[x y] (rand-nth colors)]))})

(defn run
  []
  (let [canvas (cnvs/create :width 600 :height 600 :clear-color "black")]
    (doto canvas
      cnvs/clear!)))

($ run)
