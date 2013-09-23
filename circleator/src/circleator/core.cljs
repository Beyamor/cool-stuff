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

(defn run
  []
  (let [cnvs (canvas/create :width 0 :height 0)
        load-button (create-load-button)
        files (file-channel load-button)]
    (doto ($ "body")
      (.append (:el cnvs))
      (.append
        (.append ($ "<div>") load-button)))
    (go (loop [file (<! files)]
          (when file
            (let [image (<! (read-image file))]
              (draw-image cnvs image)))
          (recur (<! files))))))

($ run)
