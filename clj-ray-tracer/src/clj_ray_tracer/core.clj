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

(defn ->color
  [r g b]
  (Color. (-> r (min 255) int)
          (-> g (min 255) int)
          (-> b (min 255) int)))

(defn color-binop
  [op ^Color c1 ^Color c2]
  (->color (op (.getRed c1) (.getRed c2))
           (op (.getGreen c1) (.getGreen c2))
           (op (.getBlue c1) (.getBlue c2))))

(def add-color (partial color-binop +))
(def multiply-colors (partial color-binop *))

(defn scale-color
  [^Color color scale]
  (->color (-> color .getRed   (* scale))
           (-> color .getGreen (* scale))
           (-> color .getBlue  (* scale))))

(defn add-scaled-color
  [base color scale]
  (add-color base (scale-color color scale)))

(defn reflect-around
  [d normal]
  (v/sub d
         (->> (v/dot normal d) (* 2) (v/scale normal))))

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

(defn create-ray
  [point direction]
  {:point point
   :direction (v/normalize direction)})

(defn point-along-ray
  [{:keys [point direction]} t]
  (->> t (v/scale direction) (v/add point)))

(defn collision-info
  [ray {:keys [shape] :as object}]
  (when-let [t (intersection shape ray)]
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
                      diffuse         (scale-color (multiply-colors (:color light) (:color object)) n-dot-l)
                      specular        (scale-color Color/WHITE
                                                   (-> light-direction
                                                       (v/scale -1)
                                                       (reflect-around normal)
                                                       (v/dot eye-direction)
                                                       (max 0)
                                                       (Math/pow 8)))]
                  [(add-color total-diffuse diffuse)
                   (add-color total-specular specular)]))))
          [Color/BLACK Color/BLACK] (:lights scene)))

(defn shoot-ray-iteration
  [ray {:keys [objects] :as scene} eye recursion-depth k]
  (-> Color/BLACK
      (->/when-let [{:keys [t object]}  (find-collision ray objects)]
        (->/let [collision-point        (point-along-ray ray t)
                 normal                 (normal-at-point (:shape object) collision-point)
                 ambient                (:color object)
                 [diffuse specular]     (calculate-diffuse-and-specular object scene eye collision-point normal)]
          (add-scaled-color ambient (:ambient k))
          (add-scaled-color diffuse (:diffuse k))
          (add-scaled-color specular (:specular k))
          (->/when (pos? recursion-depth)
            (->/let [reflection-direction (-> ray :direction (reflect-around normal))
                     reflection-ray       (create-ray (v/add collision-point normal)
                                                      reflection-direction)
                     reflection-color     (shoot-ray-iteration reflection-ray
                                                               scene eye (dec recursion-depth) k)]
              (add-scaled-color reflection-color 0.5)))))))

(defn shoot-ray
  [scene eye direction {:keys [recursion-depth k]
                               :or {recursion-depth 0}}]
  (shoot-ray-iteration
    (create-ray (:position eye) direction)
    scene eye recursion-depth k))

(defn pixel-coordinates
  [screen-width screen-height]
  (let [screen-xs (range screen-width)
        screen-ys (range screen-height)]
    (for [screen-x screen-xs
          screen-y screen-ys]
      [screen-x screen-y])))

(def half #(/ % 2))

(defn trace-pixel
  [scene {screen-width :width screen-height :height :keys [eye]} parameters [screen-x screen-y]]
  (let [x (-> screen-x (- (half screen-width)) (/ (half screen-width)))
        y (-> (half screen-height) (- screen-y) (/ (half screen-height)))
        direction (v/normalize (v3 x y -1))]
    {:x screen-x :y screen-y
     :color (shoot-ray scene eye direction parameters)}))

(defn pmap!
  [f coll]
  (->> coll
       (map #(future (f %)))
       doall
       (map deref)))

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
      doall)
    image))

(defn trace-from-file
  [file-name]
  (let [{:keys [view scene parameters]} (binding [*ns* (find-ns 'clj-ray-tracer.core)]
                               (-> file-name slurp read-string eval))]
    (trace scene view parameters)))
