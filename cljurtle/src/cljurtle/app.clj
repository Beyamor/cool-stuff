(ns cljurtle.app
  (:require [cljurtle.core :as core :refer [forward back left right new-turtle pen-color jump-to]]
            [cljurtle.draw :as draw]
            [lonocloud.synthread :as ->]
            [seesaw.core :as s]
            [seesaw.bind :as sb]))

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

(defn create-canvas
  [turtle]
  (let [width   600
        height  400
        el      (s/canvas
                  :size [width :by height]
                  :paint  (fn [c g]
                            (draw/turtle-sequence! g width height
                                                   (core/state-sequence @turtle))))]
    (sb/bind turtle
             (sb/b-do [_]
                    (s/repaint! el)))
    el))

(defn -main [& args]
  (let [turtle      (atom nil)
        canvas      (create-canvas turtle)
        script-box  (s/text
                      :multi-line?  true
                      :rows         15)]
    (s/invoke-later
      (-> (s/frame :title     "Cljurtle"
                   :content   (s/vertical-panel
                                :items  [canvas
                                         (s/scrollable script-box)])
                   :on-close  :exit)
        s/pack!
        s/show!))
    (s/invoke-later
      (reset! turtle
              (-> (new-turtle 0 -100)
                (pen-color "green")
                (fib 10))))
    nil))
