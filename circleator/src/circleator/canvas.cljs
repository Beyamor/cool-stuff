(ns circleator.canvas
  (:use [jayq.core :only [$]]))

(defn- get-el-context
  [el]
  (-> el (aget 0) (.getContext "2d")))

(defn raw-el
  [{:keys [el]}]
  (aget el 0))

(defn- set-el-dimensions!
  [el width height]
  (doto el
    (.width width)
    (.height height)
    (.attr "tabindex" "0"))
  (let [context (get-el-context el)
        context-canvas (.-canvas context)]
    (set! (.-width context-canvas) width)
    (set! (.-height context-canvas) height)))

(defn set-dimensions!
  [{:keys [el]} width height]
  (set-el-dimensions! el width height))

(defn create
  [& {:keys [width height clear-color]
      :or {clear-color "white"}}]
  (let [el ($ "<canvas>")]
    (set-el-dimensions! el width height)
    (.css el "background-color" clear-color)
    {:el el
     :context (get-el-context el)
     :width width
     :height height}))

(defn get-pixel
  [{:keys [context]} x y]
  (let [data (.-data (.getImageData context x y 1 1))]
    {:r (aget data 0)
     :g (aget data 1)
     :b (aget data 2)
     :a (aget data 3)}))

(defn data-url
  [canvas]
  (-> canvas raw-el .toDataURL))
