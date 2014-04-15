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

(defn create-sphere
  [center radius]
  {:center center
   :radius radius})

(defn create-ray
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
          :else t0)))))

(defn normal-at-point
  [sphere point]
  (->
    point
    (v/sub (:center sphere))
    v/normalize))

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
      (sort-by :t)
      first))

(defn aspect-ratio
  [view]
  (/ (:width view) (:height view)))

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
  [objects position direction]
  (shoot-ray-iteration
    objects
    (create-ray position direction)
    1))

(defn trace
  [{:keys [objects]} {screen-width :width screen-height :height :keys [eye]}]
  (let [aspect-ratio        (/ screen-width screen-height)
        half-screen-width   (/ screen-width 2)
        half-screen-height  (/ screen-height 2)
        screen-xs (range screen-width)
        screen-ys (range screen-height)]
    {:width screen-width
     :height screen-height
     :pixels (for [screen-x screen-xs
                   screen-y screen-ys
                   :let [x (-> screen-x (- half-screen-width) (/ half-screen-width))
                         y (-> half-screen-height (- screen-y) (/ half-screen-height) (* aspect-ratio))
                         direction (v/normalize (v3 x y -1))
                         ray (create-ray (:position eye) direction)
                         object (find-collision ray objects)]]
               {:x screen-x :y screen-y
                :color (shoot-ray objects (:position eye) direction)})}))

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
  (let [{:keys [view scene]} (binding [*ns* (find-ns 'clj-ray-tracer.core)]
                               (-> file-name slurp read-string eval))]
    (trace scene view)))
