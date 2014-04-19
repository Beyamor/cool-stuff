(ns tracer.repl
  (:require [tracer.core :as core])
  (:import [javax.swing JFrame ImageIcon JLabel]))

(defn show!
  [trace]
  (doto (JFrame.)
    (.add (-> trace core/generate-image ImageIcon. JLabel.))
    .pack
    .show))

(defn show-file!
  [file-name]
  (-> file-name core/trace-from-file show!))
