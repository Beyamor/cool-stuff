(ns clj-ray-tracer.core
  (:require [euclidean.math.vector :as v])
  (:import java.awt.Color))

(defn v3
  [x y z]
  (v/vector x y z))

(defn sphere
  [center radius]
  {:center center
   :radius radius})

(defn ray
  [point direction]
  {:point point
   :direction (v/normalize direction)})

(defn point-along-ray
  [{:keys [point direction]} t]
  (v/add point
         (v/scale direction t)))

(defn intersection
  [{:keys [radius] :as sphere} {:keys [direction] :as ray}]
  (let [v (v/sub (:point ray) (:center sphere))
        a (v/dot direction direction)
        b (* 2 (v/dot v direction))
        c (- (v/dot v v) (* radius radius))
        discriminant  (- (* b b) (* 4 a c))]
    (when-not (neg? discriminant)
      (let [discriminant-sqrt (Math/sqrt discriminant)
            q (if (neg? b)
                (/ (- (- b) discriminant-sqrt) 2)
                (/ (+ (- b) discriminant-sqrt) 2))
            t0 (/ q a)
            t1 (/ c q)
            [t0 t1] (if (> t0 t1)
                      [t1 t0] [t0 t1])]
        (cond
          (neg? t1) nil
          (neg? t0) t1
          :else t0) t0))))
