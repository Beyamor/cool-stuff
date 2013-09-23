(ns circleator.draw
  (:require [circleator.canvas :as canvas]))

(defn outline!
  [context color]
  (when color
    (set! (.-strokeStyle context) color)
    (.stroke context)))

(defn fill!
  [context color]
  (when color
    (set! (.-fillStyle context) color)
    (.fill context)))

(defn- fill-and-outline!
  [context fill-color outline-color]
  (doto context
    (fill! fill-color)
    (outline! outline-color)))

(defn line!
  [{:keys [context]} & {[x1 y1] :from [x2 y2] :to
                        :keys [color]}]
  (doto context
    .beginPath
    (.moveTo x1 y1)
    (.lineTo x2 y2)
    (outline! color)))

(defn rect!
  [{:keys [context]} & {:keys [x y width height fill-color outline-color color]}]
  (let [fill-color (or fill-color color)]
    (doto context
      .beginPath
      (.rect x y width height)
      (fill-and-outline! fill-color outline-color))))

(defn clear!
  [{:keys [width height context] :as canvas}]
  (.clearRect context 0 0 width height))

(defn canvas!
  [{destination :context}
   & {:keys [x y] source :canvas}]
  (doto destination
    (.drawImage (canvas/raw-el source) x y)))
