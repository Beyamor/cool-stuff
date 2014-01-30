(ns cljurtle.app
  (:require [cljurtle.core :as core]
            [cljurtle.draw :as draw]
            [lonocloud.synthread :as ->]
            [seesaw.core :as s]
            [seesaw.bind :as sb])
  (:import java.awt.Font))

(defn set-up-script-ns
  [script-ns turtles]
  (binding [*ns* script-ns]
    (eval '(do
             (clojure.core/refer-clojure)
             (require '[lonocloud.synthread :as ->])
             (require '[cljurtle.core :as core
                        :refer [move-forward move-backward turn-left turn-right
                                new-turtle set-color jump-to lower-pen
                                raise-pen get-property set-property update-property]]))))
  (intern script-ns 'def-turtle (fn [turtle]
                                  (swap! turtles conj turtle))))

(defn eval-script
  [script]
  (let [script-ns-name  (gensym)
        script-ns       (create-ns script-ns-name)
        turtles         (atom [])
        prev-ns         *ns*]
    (set-up-script-ns script-ns turtles)
    (binding [*ns*  script-ns]
      (-> (str "(do\n" script "\n)")
        read-string
        eval))
    (remove-ns script-ns-name)
    @turtles))

(defn run
  [turtles script]
  (reset! turtles
          (eval-script script)))

(defn create-canvas
  [turtles]
  (let [width   600
        height  400
        el      (s/canvas
                  :size [width :by height]
                  :paint  (fn [c g]
                            (doseq [turtle @turtles]
                              (draw/turtle-sequence! g width height
                                                     (core/state-sequence turtle)))))]
    (sb/bind turtles
             (sb/b-do [_]
                    (s/repaint! el)))
    el))

(defn create-script-box
  []
  (->
    (s/text
                      :multi-line?  true
                      :rows         15)
    (doto
      (.setFont (Font. "SansSerif" Font/PLAIN 12)))))

(defn create-run-button
  [turtles script-box]
  (s/button
    :text   "run"
    :listen [:action
                   (fn [_]
                     (run turtles (s/value script-box)))]))

(defn -main [& args]
  (let [turtles     (atom [])
        canvas      (create-canvas turtles)
        script-box  (create-script-box)
        run-button  (create-run-button turtles script-box)]
    (s/invoke-later
      (-> (s/frame :title     "Cljurtle"
                   :content   (s/vertical-panel
                                :items  [canvas
                                         (s/scrollable script-box)
                                         run-button])
                   :on-close  :exit)
        s/pack!
        s/show!))))
