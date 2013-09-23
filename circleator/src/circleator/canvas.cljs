(ns circleator.canvas
  (:use [jayq.core :only [$]]))

(defn- get-el-context
  [el]
  (-> el (aget 0) (.getContext "2d")))

(defn raw-el
  [{:keys [el]}]
  (aget el 0))

(defn- set-dimensions!
  [el width height]
  (doto el
    (.width width)
    (.height height)
    (.attr "tabindex" "0"))
  (let [context (get-el-context el)
        context-canvas (.-canvas context)]
    (set! (.-width context-canvas) width)
    (set! (.-height context-canvas) height)))

(defn create
  [& {:keys [width height clear-color]
      :or {clear-color "white"}}]
  (let [el ($ "<canvas>")]
    (set-dimensions! el width height)
    (.css el "background-color" clear-color)
    {:el el
     :context (get-el-context el)
     :width width
     :height height}))
