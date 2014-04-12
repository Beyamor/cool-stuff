(ns clj-ray-tracer.core
  (:use [clojure.java.io :only [file]])
  (:require [euclidean.math.vector :as v])
  (:import java.awt.Color
           java.awt.image.BufferedImage
           javax.imageio.ImageIO))

(defn v3
  [x y z]
  (v/vector x y z))

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
      first
      :object))

(defn aspect-ratio
  [view]
  (/ (:width view) (:height view)))

(defn trace
  [{:keys [objects]} {screen-width :width screen-height :height :keys [eye]}]
  (let [aspect-ratio        (/ screen-width screen-height)
        half-screen-width   (/ screen-width 2)
        half-screen-height  (/ screen-height 2)]
    {:width screen-width
     :height screen-height
     :pixels (for [screen-x (range screen-width)
                   screen-y (range screen-height)
                   :let [x (-> screen-x (- half-screen-width) (/ half-screen-width))
                         y (-> half-screen-height (- screen-y) (/ half-screen-height) (* aspect-ratio))
                         direction (v/normalize (v3 x y 1))
                         ray (create-ray (:position eye) direction)
                         object (find-collision ray objects)]]
               {:x screen-x :y screen-y
                :color (if object
                         (:color object)
                         Color/BLACK)})}))

(defn generate-image
  [{:keys [width height pixels]}]
  (let [image (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR)]
    (doseq [pixel pixels]
      (.setRGB image (:x pixel) (:y pixel) (.getRGB (:color pixel))))
    image))

(defn dump-trace!
  [trace file-name]
    (ImageIO/write (generate-image trace) "png" (file (str file-name ".png"))))

(defn trace!
  [scene view file-name]
  (-> (trace scene view) (dump-trace! file-name)))

(defn trace-from-file
  [file-name]
  (let [{:keys [view scene]} (-> file-name slurp read-string eval)]
    (trace scene view)))
