(ns tracer.shapes
  (:require [euclidean.math.vector :as v]))

(defprotocol Shape
  (intersection [shape ray])
  (normal-at-point [shape point]))

(defrecord Sphere [center radius]
  Shape
  (intersection
    [_ {:keys [direction] :as ray}]
    (let [v (v/sub (:point ray) center)
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
            :else t0)))))

  (normal-at-point
    [_ point]
    (-> point
        (v/sub center)
        v/normalize)))

(defn create-sphere
  [{:keys [center radius]}]
  (->Sphere (v/into-vector center) radius))

(defrecord Plane [point normal]
  Shape
  (intersection
    [_ ray]
    (let [num   (-> point (v/sub (:point ray)) (v/dot normal))
          denom (v/dot (:direction ray) normal)]
      (cond
        (and (zero? num) (zero? denom)) 0
        (zero? denom)                   nil
        :else                           (/ num denom))))

  (normal-at-point
    [_ point]
    normal))

(defn create-plane
  [{:keys [point normal]}]
  (->Plane (v/into-vector point) (v/normalize (v/into-vector normal))))
