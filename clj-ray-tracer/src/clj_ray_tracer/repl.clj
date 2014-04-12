(ns clj-ray-tracer.repl
  (:require [clj-ray-tracer.core :as core])
  (:import [javax.swing JFrame ImageIcon JLabel]))

(defn show!
  [trace]
  (doto (JFrame.)
    (.add (-> trace core/generate-image ImageIcon. JLabel.))
    .pack
    .show))
