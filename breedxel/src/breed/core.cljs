(ns breed.core
  (:use [jayq.core :only [$]]
        [cljs.core.async :only [chan <! put!]])
  (:require [breed.canvas :as cnvs])
  (:use-macros [cljs.core.async.macros :only [go]]))

(defn create-xel
  [columns rows colors]
  {:columns columns
   :rows rows
   :cells (into {}
                 (for [x (range columns)
                       y (range rows)]
                   [[x y] (rand-nth colors)]))})

(defn create-xel-set
  [& {:keys [columns rows xel-columns xel-rows colors]}]
  {:columns columns
   :rows rows
   :xels (into {}
               (for [i (range columns)
                     j (range rows)]
                 [[i j] (create-xel xel-columns xel-rows colors)]))})

(defn create-xels-view
  [& {:keys [xels width height border]
      :or {boder 0}}]
   {:width width
    :height height
    :xels xels
    :border border})

(defn view-selection
  [view pixel-x pixel-y]
  [(Math/floor (/ pixel-x
                  (/ (:width view) (get-in view [:xels :columns]))))
   (Math/floor (/ pixel-y
                  (/ (:height view) (get-in view [:xels :rows]))))])

(defn draw-xel!
  [canvas {:keys [columns rows] :as xel}
   & {:keys [x y width height border]
      :or {border 0}}]
  (let [border2 (* border 2)
        cell-width (/ (- width border2) columns)
        cell-height (/ (- height border2) rows)]
    (doseq [i (range columns)
            j (range rows)]
      (doto canvas
        (cnvs/draw-rect!
          :x (+ x border (* i cell-width))
          :y (+ y border (* j cell-height))
          :width cell-width
          :height cell-height
          :color (get-in xel [:cells [i j]]))))))

(defn draw-xels!
  [canvas  {{:keys [columns rows] :as xels} :xels
            :keys [width height border]}]
  (let [xel-width (/ width columns)
        xel-height (/ height rows)]
    (doseq [i (range columns)
            j (range rows)]
      (doto canvas
        (draw-xel! (get-in xels [:xels [i j]])
                  :x (* i xel-width)
                  :y (* j xel-height)
                  :width xel-width
                  :height xel-height
                  :border border)))))

(defn watch-mouse-events
  [$el]
  (let [c (chan)
        push-event (fn [event-type]
                     (fn [e]
                       (let [which (case (.-which e)
                                     1 :left
                                     2 :middle
                                     3 :right)
                             parent-offset (.. $el parent offset)
                             x (- (.-pageX e) (.-left parent-offset))
                             y (- (.-pageY e) (.-top parent-offset))]
                         (put! c [event-type which [x y]]))))]
    (doto $el
      (.mousedown (push-event :mouse-down))
      (.mouseup (push-event :mouse-up)))
    c))

(defn run
  []
  (let [canvas (cnvs/create :width 600 :height 600
                            :clear-color "pink"
                            :parent "#app")
        xel-width 3
        xel-height 3
        xels (create-xel-set
               :columns 3
               :rows 3
               :xel-columns 32
               :xel-rows 32
               :colors #{"black" "white"})
        xels-view (create-xels-view :xels xels :width 600 :height 600 :border 10)
        mouse-events (watch-mouse-events (:el canvas))]
    (doto canvas
      cnvs/clear!
      (draw-xels! xels-view))
    (go (loop [[event-type which [x y]] (<! mouse-events)]
          (case [event-type which]
            [:mouse-down :left]
            (let [selection (view-selection xels-view x y)]
              (.log js/console (str selection))
              (recur (<! mouse-events)))

            (recur (<! mouse-events)))))))

($ run)
