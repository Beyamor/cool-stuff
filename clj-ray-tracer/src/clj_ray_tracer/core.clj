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
  {:objects [{:shape (create-sphere (v3 0 0 -20) 5)
              :color Color/RED}]})

(def view
  {:width 800
   :height 600})

(defn trace
  [{:keys [objects]} {:keys [width height]}]
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
