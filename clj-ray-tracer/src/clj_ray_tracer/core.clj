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
          :else t0) t0))))

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

(def scene
  {:eye (v3 0 0 20)
   :objects [{:shape (create-sphere (v3 0 0 -20) 5)
              :color Color/RED}]})

(def screen
  {:width 800
   :height 600})

(defn trace
  [{:keys [width height]} {:keys [objects]}]
  {:width width
   :height height
   :pixels (for [x (range width)
                       y (range height)]
                   (let [ray (create-ray (v3 (- x (/ width 2))
                                             (- (/ height 2) y)
                                             0)
                                         (v3 0 0 -1))
                         object (find-collision ray objects)]
                     {:x x :y y
                      :color (if object
                               (:color object)
                               Color/BLACK)}))})

(defn dump-trace!
  [{:keys [width height pixels]} file-name]
  (let [image (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR)]
    (doseq [pixel pixels]
      (.setRGB image (:x pixel) (:y pixel) (.getRGB (:color pixel))))
    (ImageIO/write image "png" (file (str file-name ".png")))))

(defn trace!
  [screen scene file-name]
  (-> (trace screen scene) (dump-trace! file-name)))
