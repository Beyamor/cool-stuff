(ns cljurtle.app
  (:require [cljurtle.core :as core]
            [cljurtle.draw :as draw]
            [lonocloud.synthread :as ->]
            [seesaw.core :as s]
            [seesaw.bind :as sb]))

(comment
  (defn fib
    [turtle depth]
    (-> turtle
      (move-forward 30)
      (->/when (> depth 2)
               (turn-left 15)
               (fib (- depth 1))
               (turn-right 30)
               (fib (- depth 2))
               (turn-left 15))
      (move-backward 30)))
  )

(comment
  (defn fern
    [turtle size]
    (-> turtle
      (->/when (> size 4)
               (move-forward (/ size 25))
               (turn-left 90)  (fern (* size 0.3))
               (turn-right 90)
               (turn-right 90) (fern (* size 0.3))
               (turn-left 90)  (fern (* size 0.85))
               (move-backward (/ size 25)))))
  )

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
      (-> (str "(do " script ")")
        read-string
        eval))
    (remove-ns script-ns-name)
    @turtles))

(defn run
  [turtles script]
  (reset! turtles
          (eval-script script)))

(defn -main [& args]
  (let [turtles     (atom [])
        canvas      (create-canvas turtles)
        script-box  (s/text
                      :multi-line?  true
                      :rows         15)
        run-button  (s/button
                      :text         "run"
                      :listen       [:action
                                     (fn [_]
                                       (run turtles (s/value script-box)))])]
    (s/invoke-later
      (-> (s/frame :title     "Cljurtle"
                   :content   (s/vertical-panel
                                :items  [canvas
                                         (s/scrollable script-box)
                                         run-button])
                   :on-close  :exit)
        s/pack!
        s/show!))))
