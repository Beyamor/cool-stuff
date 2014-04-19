(ns tracer.core
  (:use [clojure.java.io :only [file]]
        tracer.util)
  (:require [euclidean.math.vector :as v]
            [lonocloud.synthread :as ->]
            [tracer.color :as color]
            [tracer.shapes :as shape])
  (:import [java.awt Color Toolkit]
           [java.awt.image BufferedImage MemoryImageSource]
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)

(defn create-ray
  [point direction]
  {:point point
   :direction (v/normalize direction)})

(defn point-along-ray
  [{:keys [point direction]} t]
  (->> t (v/scale direction) (v/add point)))

(defn collision-info
  [ray {:keys [shape] :as object}]
  (when-let [t (shape/intersection shape ray)]
    {:t t
     :object object}))

(defn find-collision
  [ray objects]
  (->> objects
       (map #(collision-info ray %))
       (filter #(and % (-> % :t pos?)))
       (sort-by :t)
       first))

(defn calculate-diffuse-and-specular
  [object scene eye collision-point normal]
  (reduce (fn [[total-diffuse total-specular] light]
            (let [light-direction (-> (:position light) (v/sub collision-point) v/normalize)
                  n-dot-l         (v/dot light-direction normal)]
              (if (< n-dot-l 0)
                [total-diffuse total-specular]
                (let [eye-direction   (-> (:position eye) (v/sub collision-point) v/normalize)
                      shadowed?       (-> (create-ray (v/add collision-point normal)
                                                      light-direction)
                                          (find-collision (:objects scene)))
                      diffuse         (color/scale (color/multiply (:color light) (:color object))
                                                   (-> n-dot-l
                                                       (->/when shadowed?
                                                         (* 0.5))))
                      specular        (color/scale Color/WHITE
                                                   (-> light-direction
                                                       (v/scale -1)
                                                       (reflect-around normal)
                                                       (v/dot eye-direction)
                                                       (max 0)
                                                       (Math/pow 8)))]
                  [(color/add total-diffuse diffuse)
                   (color/add total-specular specular)]))))
          [Color/BLACK Color/BLACK] (:lights scene)))

(defn shoot-ray-iteration
  [ray {:keys [objects] :as scene} eye reflection-depth k]
  (-> Color/BLACK
      (->/when-let [{:keys [t object]}  (find-collision ray objects)]
        (->/let [collision-point        (point-along-ray ray t)
                 normal                 (shape/normal-at-point (:shape object) collision-point)
                 ambient                (:color object)
                 [diffuse specular]     (calculate-diffuse-and-specular object scene eye collision-point normal)]
          (color/add-scaled ambient (:ambient k))
          (color/add-scaled diffuse (:diffuse k))
          (color/add-scaled specular (:specular k))
          (->/when (pos? reflection-depth)
            (->/let [reflection-direction (-> ray :direction (reflect-around normal))
                     reflection-ray       (create-ray (v/add collision-point normal)
                                                      reflection-direction)
                     reflection-color     (shoot-ray-iteration reflection-ray
                                                               scene eye (dec reflection-depth) k)]
              (color/add-scaled reflection-color 0.2)))))))

(defn shoot-ray
  [scene eye direction {:keys [reflection-depth k]
                        :or {reflection-depth 0}}]
  (shoot-ray-iteration
    (create-ray (:position eye) direction)
    scene eye reflection-depth k))

(defn pixel-coordinates
  [screen-width screen-height]
  (let [pixel-xs (range screen-width)
        pixel-ys (range screen-height)]
    (for [pixel-x pixel-xs
          pixel-y pixel-ys]
      [pixel-x pixel-y])))

(defn ray-offsets
  [antialiasing-on?]
  (if antialiasing-on?
    [[0.5 0.5] [0.5 0] [0.5 1] [0 0.5] [1 0.5]]
    [[0.5 0.5]]))

(defn trace-pixel
  [scene {screen-width :width screen-height :height :keys [eye]} parameters [pixel-x pixel-y]]
  {:x pixel-x :y pixel-y
   :color (color/average
            (for [[x-offset y-offset] (-> parameters :antialiasing ray-offsets)
                  :let [x (-> (+ pixel-x x-offset) (- (half screen-width)) (/ (half screen-width)))
                        y (-> (half screen-height) (- (+ pixel-y y-offset)) (/ (half screen-height)))
                        direction (v/normalize (v/vector x y -1))]]
              (shoot-ray scene eye direction parameters)))})

(defn trace
  [scene {:keys [width height] :as view} parameters]
  {:width width
   :height height
   :pixels (->> (pixel-coordinates width height)
                (partition-all 80000)
                (pmap! #(->> %
                             (map (partial trace-pixel scene view parameters))
                             doall))
                (apply concat))})

(defn generate-image
  [{:keys [width height pixels]}]
  (let [image (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR)]
    (->>
      pixels
      (partition-all 80000)
      (pmap! (fn [pixels]
               (doseq [{:keys [x y ^Color color]} pixels]
                 (->> color .getRGB (.setRGB image x y)))))
      dorun)
    image))

(defn trace-from-file
  [file-name]
  (let [{:keys [view scene parameters]} (binding [*ns* (find-ns 'tracer.core)]
                                          (-> file-name slurp read-string eval))]
    (trace scene view parameters)))
