(ns tracer.color
  (:import java.awt.Color))

; Needed to eval Colors
(defmethod print-dup java.awt.Color
  [^Color color ^java.io.Writer stream]
  (.write stream
          (str "#=(java.awt.Color. " (.getRGB color) ")")))

(defn ->color
  [r g b]
  (Color. (-> r (min 255) int)
          (-> g (min 255) int)
          (-> b (min 255) int)))

(defn parse
  [color-name]
  (-> Color (.getField color-name) (.get nil)))

(defn binop
  [op ^Color c1 ^Color c2]
  (->color (op (.getRed c1) (.getRed c2))
           (op (.getGreen c1) (.getGreen c2))
           (op (.getBlue c1) (.getBlue c2))))

(def add (partial binop +))
(def multiply (partial binop *))

(defn scale
  [^Color color scale-factor]
  (->color (-> color .getRed   (* scale-factor))
           (-> color .getGreen (* scale-factor))
           (-> color .getBlue  (* scale-factor))))

(defn add-scaled
  [base color scale-factor]
  (add base (scale color scale-factor)))

(defn average
  [colors]
  (let [[r g b] (reduce (fn [[r g b] ^Color color]
                          [(+ r (.getRed color))
                           (+ g (.getGreen color))
                           (+ b (.getBlue color))])
                        [0 0 0] colors)]
    (->color (/ r (count colors))
             (/ g (count colors))
             (/ b (count colors)))))
