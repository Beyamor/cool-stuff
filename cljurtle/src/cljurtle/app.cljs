(ns cljurtle.app
  (:require [cljurtle.core :as core :refer [forward back left right new-turtle pen-color jump-to]]
            [cljurtle.draw :as draw])
  (:require-macros [lonocloud.synthread :as ->]))

(defn get-canvas
  [id]
  (let [el (.getElementById js/document id)]
    {:context (.getContext el "2d")
     :width   (.-width el)
     :height  (.-height el)}))

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

(set! (.-onload js/window)
      #(let [turtle (-> (new-turtle -50 -100)
                      (pen-color "green")
                      (fern 1500)
                      (pen-color "blue")
                      (jump-to 50 -150)
                      (fern 1000))]
         (draw/turtle!
           (get-canvas "canvas")
           turtle)))
