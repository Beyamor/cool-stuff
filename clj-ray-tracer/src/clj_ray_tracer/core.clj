(ns clj-ray-tracer.core
  (:use [clojure.java.io :only [file]])
  (:require [euclidean.math.vector :as v]
            [lonocloud.synthread :as ->])
  (:import [java.awt Color Toolkit]
           [java.awt.image BufferedImage MemoryImageSource]
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)

(defn v3
  [x y z]
  (v/vector x y z))

(defn add-color
  [^Color base ^Color color scale]
  (Color.
    (-> color .getRed (* scale) (+ (.getRed base)) (min 255) int)
    (-> color .getGreen (* scale) (+ (.getGreen base)) (min 255) int)
    (-> color .getBlue (* scale) (+ (.getBlue base)) (min 255) int)))

(defn reflect-around-normal
  [d normal]
  (v/sub d
         (v/scale normal (* 2 (v/dot d normal)))))

(defprotocol Shape
  (intersection [shape ray])
  (normal-at-point [shape point]))

(defrecord Sphere
  [center radius]
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

(def create-sphere ->Sphere)

(defrecord Plane
  [point normal]
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

(defn create-ray
  [point direction]
  {:point point
   :direction (v/normalize direction)})

(defn point-along-ray
  [{:keys [point direction]} t]
  (v/add point
         (v/scale direction t)))

(defn collision-info
  [ray {:keys [shape] :as object}]
  (when-let [t (intersection shape ray)]
    {:t t
     :object object}))

(defn find-collision
  [ray objects]
  (->> objects
      (map #(collision-info ray %))
      (filter identity)
      (filter #(-> % :t pos?))
      (sort-by :t)
      first))

(defn shoot-ray-iteration
  [objects ray recursion-depth]
  (->
    Color/BLACK
    (->/when-let [{:keys [t object]} (find-collision ray objects)]
      (add-color (object :color) 0.5)
      (->/when (pos? recursion-depth)
        (->/let [recur-start (point-along-ray ray t)
                 normal (normal-at-point (:shape object) recur-start)
                 recur-direction (reflect-around-normal (:direction ray) normal)]
          (add-color
            (shoot-ray-iteration objects
                                 (create-ray (v/add recur-start recur-direction)
                                             recur-direction)
                                 (dec recursion-depth))
            0.5))))))

(defn shoot-ray
  [objects position direction {:keys [recursion-depth]
                               :or {recursion-depth 0}}]
  (shoot-ray-iteration
    objects
    (create-ray position direction)
    recursion-depth))

(defn pixel-coordinates
  [screen-width screen-height]
  (let [screen-xs (range screen-width)
        screen-ys (range screen-height)]
    (for [screen-x screen-xs
          screen-y screen-ys]
      [screen-x screen-y])))

(def half #(/ % 2))

(defn trace-pixel
  [objects screen-width screen-height eye parameters [screen-x screen-y]]
  (let [x (-> screen-x (- (half screen-width)) (/ (half screen-width)))
        y (-> (half screen-height) (- screen-y) (/ (half screen-height)))
        direction (v/normalize (v3 x y -1))]
    {:x screen-x :y screen-y
     :color (shoot-ray objects (:position eye) direction parameters)}))

(defn pmap!
  [f coll]
  (->> coll
       (map #(future (f %)))
       doall
       (map deref)))

(defn trace
  [{:keys [objects]} {screen-width :width screen-height :height :keys [eye]} parameters]
  {:width screen-width
   :height screen-height
   :pixels (->> (pixel-coordinates screen-width screen-height)
                (partition-all (/ (* screen-width screen-height) 8))
                (pmap! #(->> %
                             (map (partial trace-pixel
                                           objects screen-width screen-height eye parameters))
                             doall))
                (apply concat))})

(defn generate-image
  [{:keys [width height pixels]}]
  (let [image (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR)]
    (->>
      pixels
      (partition-all (/ (* width height) 32))
      (pmap (fn [pixels]
              (doseq [{:keys [x y ^Color color]} pixels]
                (->> color .getRGB (.setRGB image x y)))))
      doall)
    image))

(defn dump-trace!
  [trace file-name]
    (ImageIO/write (generate-image trace) "png" (file (str file-name ".png"))))

(defn trace!
  [scene view file-name]
  (-> (trace scene view) (dump-trace! file-name)))

(defn trace-from-file
  [file-name]
  (let [{:keys [view scene parameters]} (binding [*ns* (find-ns 'clj-ray-tracer.core)]
                               (-> file-name slurp read-string eval))]
    (trace scene view parameters)))
