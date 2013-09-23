(ns circleator.core
  (:use [jayq.core :only [$]]
        [cljs.core.async :only [chan sliding-buffer put!]])
  (:require [circleator.canvas :as canvas]
            [circleator.draw :as draw])
  (:use-macros [cljs.core.async.macros :only [go]]))

(set! *print-fn*
      (fn [& args]
        (->> args (map str) (interpose " ") (apply str) (.log js/console))))

(defn create-load-button
  []
  (doto ($ "<input>")
    (.attr "type" "file")))

(defn create-download-button
  []
  (doto ($ "<a>")
    (.text "Download")
    .hide))

(defn file-channel
  [load-button]
  (let [files (chan (sliding-buffer 1))]
    (.change load-button
             (fn [e]
               (-> e .-target .-files (aget 0) (->> (put! files)))))
    files))

(defn read-file
  [file]
  (let [c (chan)
        file-reader (js/FileReader.)]
    (set! (.-onload file-reader)
          #(put! c (-> % .-target .-result)))
    (.readAsDataURL file-reader file)
    (go (<! c))))

(defn load-image
  [file-data]
  (let [c (chan)
        image (js/Image.)]
    (set! (.-onload image)
          #(put! c image))
    (set! (.-src image) file-data)
    (go (<! c))))

(defn read-image
  [file]
  (go
    (-> file
      read-file <!
      load-image <!)))

(defn draw-image
  [cnvs image]
  (doto cnvs
    (canvas/set-dimensions! (.-width image) (.-height image))
    draw/clear!
    (draw/image! :image image)))

(defn image->canvas
  [image]
  (doto (canvas/create
          :width (.-width image)
          :height (.-height image))
    (draw/image! :image image)))

(defn circleate
  [cnvs image]
  (doto cnvs
    draw/clear!
    (canvas/set-dimensions! (:width image) (:height image)))
  (let [distribution 0.01
        number-of-points (* distribution (:width image) (:height image))
        min-radius 20
        max-radius 10
        alpha 100]
    (dorun
      (for [i (range number-of-points)
            :let [x (rand-int (:width image))
                  y (rand-int (:height image))
                  radius (+ min-radius (rand (- max-radius min-radius)))
                  color (-> image
                          (canvas/get-pixel x y)
                          (assoc :a alpha)
                          draw/rgba-color)]]
        (draw/rect! cnvs
                    :x (- x radius) :y (- y radius)
                    :width (* 2 radius) :height (* 2 radius)
                    :color color)))))

(defn div-wrapped
  [el]
  (.append ($ "<div>") el))

(def counter (atom 0))

(defn next-image-title
  []
  (str "wits-" (swap! counter inc)))

(defn run
  []
  (let [cnvs (canvas/create :width 0 :height 0)
        load-button (create-load-button)
        download-button (create-download-button)
        files (file-channel load-button)]
    (doto ($ "body")
      (.append (:el cnvs))
      (.append (div-wrapped load-button))
      (.append (div-wrapped download-button)))
    (go (loop [file (<! files)]
          (.hide download-button)
          (when file
            (let [image (image->canvas (<! (read-image file)))]
              (circleate cnvs image)
              (doto download-button
                (.attr "href" (canvas/data-url cnvs))
                (.attr "download" (next-image-title))
                .show)))
          (recur (<! files))))))

($ run)
