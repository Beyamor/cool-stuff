(ns cljurtle.app
  (:require [cljurtle.core :as core :refer [forward back left right new-turtle]]
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

(set! (.-onload js/window)
      #(let [turtle (-> new-turtle
                      (fib 10))]
         (draw/turtle!
           (get-canvas "canvas")
           (core/state-sequence turtle))))
