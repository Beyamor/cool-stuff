(ns linden.core
  (:use [clojure.string :only [split trim]]
        seesaw.core))

(defn parse-rule
  [input]
  (let [[pred succ] (split input #"->")
        pred (trim pred)
        succ (->> succ (map trim) (remove empty?))]
    [pred succ]))

(defn -main
  [& args]
  (invoke-later
    (->
      (frame
        :title "L-Systems"
        :content "L-Systems go!"
        :minimum-size [800 :by 600]
        :on-close :exit)
      pack!
      show!)))
