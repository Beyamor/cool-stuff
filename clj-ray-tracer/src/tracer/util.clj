(ns tracer.util
  (:require [euclidean.math.vector :as v]))

(defn pmap!
  "Like pmap, but it forces the (parallel) evaluation of the mapping."
  [f coll]
  (->> coll
       (map #(future (f %)))
       doall
       (map deref)))

(defn reflect-around
  [d normal]
  (v/sub d
         (->> (v/dot normal d) (* 2) (v/scale normal))))

(def half #(/ % 2))
