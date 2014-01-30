(ns cljurtle.app
  (:require [cljurtle.core :as core :refer [forward back left right new-turtle pen-color jump-to]]
            [cljurtle.draw :as draw]
            [lonocloud.synthread :as ->]
            [seesaw.core :as s]))

(defn fib
  [turtle depth]
  (-> turtle
    (forward 30)
    (->/when (> depth 2)
             (left 15)
             (fib (- depth 1))
             (right 30)
             (fib (- depth 2))
             (left 15))
    (back 30)))

(defn fern
  [turtle size]
  (-> turtle
    (->/when (> size 4)
             (forward (/ size 25))
             (left 90)  (fern (* size 0.3))
             (right 90)
             (right 90) (fern (* size 0.3))
             (left 90)  (fern (* size 0.85))
             (back (/ size 25)))))

(defn -main [& args]
  (let [canvas (s/canvas
                 :size [600 :by 600])]
  (s/invoke-later
    (-> (s/frame :title     "Cljurtle"
                 :content   canvas
                 :on-close  :exit)
      s/pack!
      s/show!))))
