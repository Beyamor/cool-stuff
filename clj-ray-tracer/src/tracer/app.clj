(ns tracer.app
  (:use [tracer.core :only [trace-from-file generate-image]])
  (:import javax.imageio.ImageIO java.io.File))

(defn -main
  [input-file-name output-file-name]
  (-> input-file-name
      trace-from-file
      generate-image
      (ImageIO/write "png" (File. (str output-file-name ".png")))))
