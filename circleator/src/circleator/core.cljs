(ns circleator.core
  (:use [jayq.core :only [$]])
  (:require [circleator.canvas :as canvas]
            [circleator.draw :as draw]))

(defn run
  []
  (let [cnvs (canvas/create :width 800 :height 600)]
    (.append ($ "body") (:el cnvs))
    (doto cnvs
      (draw/rect! :x 50 :height 50 :width 100 :height 100 :color "red"))))

($ run)
