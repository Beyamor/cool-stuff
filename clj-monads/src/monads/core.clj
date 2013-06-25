(ns monads.core)

(def ^:dynamic m-result)
(def ^:dynamic m-bind)
(def ^:dynamic m-zero)
(def ^:dynamic m-plus)

(def monad-definitions (atom {}))

(defmacro defmonad
  [monad monad-definitions]
  `(swap! monad-definitions assoc '~monad ~monad-definitions))

(defmacro with-monad
  [monad & body]
  `(let [monad-definitions# (or (get @monad-definitions '~monad)
                             (throw (Exception. (str "Unknown monad " (name '~monad)))))]
     (binding [~'m-result (:result monad-definitions#)
               ~'m-bind (:bind monad-definitions#)
               ~'m-zero (:zero monad-definitions#)
               ~'m-plus (:plus monad-definitions#)]
       ~@body)))

(defmacro domonad
  [monad bindings body]
  `(with-monad ~monad
               ~(reduce
                   (fn [inner-expression [binding-symbol mv]]
                     `(m-bind ~mv (fn [~binding-symbol] ~inner-expression)))
                   body
                   (partition 2 bindings))))

(defmonad sequence-m
          {:result (fn [x] [x])
           :bind (fn [xs mf] (mapcat mf xs))
           :zero []
           :plus (fn [xs ys] (concat xs ys))})

(defmonad maybe-m
         {:result (fn [x] x)
          :bind (fn [mv mf] (when mv (mf mv)))
          :zero nil
          :plus (fn [x y] (or x y))})

(defmonad writer-m
          {:result (fn [x] [x ""])
           :bind (fn [[x log] mf]
                   (let [[x' log-addition] (mf x)]
                     [x' (str log log-addition)]))})
