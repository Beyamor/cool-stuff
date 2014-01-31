(ns cljurtle.core
  (:require [lonocloud.synthread :as ->]))

(defn degrees->rad
  [degrees]
  (-> degrees (* Math/PI) (/ 180)))

(defn new-turtle
  ([]
   (new-turtle 0 0))
  ([x y]
   {:state   {:position  {:x x :y y}
              :bearing   (/ Math/PI 2)
              :pen-down? true
              :pen-color "black"}
    :history []}))

(defn- push-history
  [{:keys [state] :as turtle}]
  (update-in turtle [:history] conj state))

(defn set-property
  [turtle property value]
  (-> turtle
    push-history
    (assoc-in [:state property] value)))

(defn update-property
  [turtle property f & args]
  (-> turtle
    push-history
    (update-in [:state property] #(apply f % args))))

(defn get-property
  [turtle property]
  (get-in turtle [:state property]))

(defn move-forward
  [{{:keys [bearing]} :state :as turtle} distance]
  (update-property turtle :position
                   #(-> %
                      (->/in [:x] (+ (* distance (Math/cos bearing))))
                      (->/in [:y] (+ (* distance (Math/sin bearing)))))))

(defn move-backward
  [turtle distance]
  (move-forward turtle (* -1 distance)))

(defn turn-left 
  [turtle degrees]
  (update-property turtle :bearing + (degrees->rad degrees)))

(defn turn-right
  [turtle degrees]
  (update-property turtle :bearing - (degrees->rad degrees)))

(defn lower-pen
  [turtle]
  (set-property turtle :pen-down? true))

(defn raise-pen
  [turtle]
  (set-property turtle :pen-down? false))

(defn jump-to
  [turtle x y]
  (-> turtle
    raise-pen
    (set-property :position {:x x :y y})
    (set-property :pen-down? (-> turtle :state :pen-down?))))

(defn set-color
  [turtle color]
  (set-property turtle :pen-color color))

(defn state-sequence
  [{:keys [state history]}]
  (conj history state))
