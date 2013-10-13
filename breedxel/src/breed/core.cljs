(ns breed.core
  (:use [jayq.core :only [$]]
        [cljs.core.async :only [chan <! put! timeout]])
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
   :colors colors
   :xels (into {}
               (for [i (range columns)
                     j (range rows)]
                 [[i j] (create-xel xel-columns xel-rows colors)]))})

(defn both-parents-set?
  [xels]
  (and (:mother xels) (:father xels)))

(defn update-parent
  [xels xy]
  (cond 
    (nil? (:mother xels))
    (assoc xels :mother xy)

    (= (:mother xels) xy)
    (dissoc xels :mother)

    :else
    (assoc xels :father xy)))

(defn breed
  [colors {:keys [columns rows] :as mother} father]
  {:columns columns
   :rows rows
   :cells (into {}
                (for [i (range columns)
                      j (range rows)]
                  [[i j] (if (< (Math/random) 0.05)
                           (rand-nth colors)
                           (if (< (Math/random) 0.5)
                             (get-in mother [:cells [i j]])
                             (get-in father [:cells [i j]])))]))})

(defn breed-next-generation
  [{:keys [columns rows colors] :as xels}]
  (let [mother (get-in xels [:xels (:mother xels)])
        father (get-in xels [:xels (:father xels)])]
    (-> xels
      (dissoc :mother)
      (dissoc :father)
      (assoc :xels (into {}
                         (for [i (range columns)
                               j (range rows)]
                           [[i j] (breed colors mother father)]))))))

(defn create-xels-view
  [& {:keys [width height border selected unselected]
      :or {boder 0 selected "white" unselected "black"}}]
   {:width width
    :height height
    :border border
    :selected selected
    :unselected unselected})

(defn xel-pixel-width
  [view xels]
  (/ (:width view) (:columns xels)))

(defn xel-pixel-height
  [view xels]
  (/ (:height view) (:rows xels)))

(defn view-selection
  [view xels pixel-x pixel-y]
  [(Math/floor (/ pixel-x (xel-pixel-width view xels)))
   (Math/floor (/ pixel-y (xel-pixel-height view xels)))])

(def xel-image
  (memoize
    (fn [{:keys [columns rows cells]} width height]
      (let [canvas (cnvs/create :width width :height height)
            cell-width (/ width columns)
            cell-height (/ height rows)]
        (doseq [i (range columns)
                j (range rows)]
          (doto canvas
            (cnvs/draw-rect!
              :x (* i cell-width)
              :y (* j cell-height)
              :width cell-width
              :height cell-height
              :color (get cells [i j]))))
        canvas))))

(defn draw-xel!
  [canvas {:keys [columns rows] :as xel}
   & {:keys [x y width height]
      :or {border 0}}]
  (let [image (xel-image xel width height)]
    (doto canvas
      (cnvs/draw-canvas! image :x x :y y))))

(defn selected-parent?
  [xels col-row]
  (or (= col-row (:mother xels))
      (= col-row (:father xels))))

(defn draw-xels!
  [canvas
   {:keys [columns rows] :as xels}
   {:keys [border] :as view}]
  (let [xel-width (xel-pixel-width view xels)
        xel-height (xel-pixel-height view xels)
        border2 (* border 2)]
    (doseq [i (range columns)
            j (range rows)]
      (doto canvas
        (cnvs/draw-rect!
          :x (* i xel-width)
          :y (* j xel-height)
          :width xel-width
          :height xel-height
          :color (if (selected-parent? xels [i j])
                   (:selected view)
                   (:unselected view)))
        (draw-xel! (get-in xels [:xels [i j]])
                  :x (+ (* i xel-width) border)
                  :y (+ (* j xel-height) border)
                  :width (- xel-width border2)
                  :height (- xel-height border2))))))

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
               :xel-columns 16
               :xel-rows 16
               :colors #{"#FF4848" "#FFFF84"})
        view (create-xels-view :width 600 :height 600 :border 5 :selected "#6CC7F8")
        mouse-events (watch-mouse-events (:$el canvas))]
    (doto canvas
      cnvs/clear!
      (draw-xels! xels view))
    (go (loop [[event-type which [x y]] (<! mouse-events), xels xels]
          (case [event-type which]
            [:mouse-down :left]
            (let [xels (->>
                         (view-selection view xels x y)
                         (update-parent xels))]
              (draw-xels! canvas xels view)
              (if (both-parents-set? xels)
                (let [_ (<! (timeout 50)) ; HACK: force redraw
                      next-generation (breed-next-generation xels)]
                  (draw-xels! canvas next-generation view)
                  (recur (<! mouse-events) next-generation))
                (recur (<! mouse-events) xels)))

            [:mouse-up :right]
            (let [xel (get-in xels [:xels (view-selection view xels x y)])
                  xel-canvas (cnvs/create :width 600 :height 600)]
              (cnvs/draw-canvas! xel-canvas (xel-image xel 600 600))
              (.open js/window
                     (-> xel-canvas :el (.toDataURL "image/png")))
              (recur (<! mouse-events) xels))

            (recur (<! mouse-events) xels))))))

($ run)
