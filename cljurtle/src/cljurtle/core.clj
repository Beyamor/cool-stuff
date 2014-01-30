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

(defn- set-property
  [turtle property value]
  (-> turtle
    push-history
    (assoc-in [:state property] value)))

(defn- update-property
  [turtle property f & args]
  (-> turtle
    push-history
    (update-in [:state property] #(apply f % args))))

(defn forward
  [{{:keys [bearing]} :state :as turtle} distance]
  (update-property turtle :position
                   #(-> %
                      (update-in [:x] + (* distance (Math/cos bearing)))
                      (update-in [:y] + (* distance (Math/sin bearing))))))

(defn back
  [turtle distance]
  (forward turtle (* -1 distance)))

(defn left 
  [turtle degrees]
  (update-property turtle :bearing + (degrees->rad degrees)))

(defn right
  [turtle degrees]
  (update-property turtle :bearing - (degrees->rad degrees)))

(defn pen-down
  [turtle]
  (set-property turtle :pen-down? true))

(defn pen-up
  [turtle]
  (set-property turtle :pen-down? false))

(defn state-sequence
  [{:keys [state history]}]
  (conj history state))

(defn jump-to
  [turtle x y]
  (-> turtle
    pen-up
    (set-property :position {:x x :y y})
    (set-property :pen-down? (-> turtle :state :pen-down?))))

(defn pen-color
  [turtle color]
  (set-property turtle :pen-color color))
