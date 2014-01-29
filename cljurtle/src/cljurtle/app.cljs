(ns cljurtle.app
  (:require [cljurtle.core :as core]
            [cljurtle.draw :as draw]))

(defn get-canvas
  [id]
  (let [el (.getElementById js/document id)]
    {:context (.getContext el "2d")
     :width   (.-width el)
     :height  (.-height el)}))

(set! (.-onload js/window)
      #(let [turtle (-> core/new-turtle
                      (core/forward 40)
                      (core/turn-right 45)
                      (core/forward 40)
                      (core/turn-left 45)
                      (core/forward 40))]
         (draw/turtle!
           (get-canvas "canvas")
           (core/state-sequence turtle))))
