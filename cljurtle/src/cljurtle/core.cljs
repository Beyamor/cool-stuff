(ns cljurtle.core)

(set! (.-onload js/window)
         #(js/alert "hello world"))
