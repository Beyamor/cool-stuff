(ns breed.canvas
  (:use [jayq.core :only [$]]))

(defn create
  [& {:keys [width height clear-color]
      :or {clear-color "white"}}]
  (let [canvas ($ "<canvas>")
        context (-> canvas (aget 0) (.getContext "2d"))]
    (doto canvas
      (.width width)
      (.height height)
      (.css "background-color" clear-color))
    (set! (.-width (.-canvas context)) width)
    (set! (.-height (.-canvas context)) height)
    (.append ($ "body") canvas)
    {:el canvas
     :context context
     :width width
     :height height}))

(defn draw-rect!
  [{:keys [context]} & {:keys [x y width height color]}]
  (doto context
    .beginPath
    (#(set! (.-fillStyle %) color))
    (.fillRect x y width height)))

(defn clear!
  [{:keys [context width height]}]
  (.clearRect context 0 0 width height))
