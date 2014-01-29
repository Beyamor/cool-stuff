(ns cljurtle.core
  (:require-macros [lonocloud.synthread :as ->]))

(defn degrees->rad
  [degrees]
  (-> degrees (* Math/PI) (/ 180)))

(def origin
  {:x 0
   :y 0})

(def new-turtle
  {:state   {:position  origin
             :origin    origin
             :bearing   (/ Math/PI 2)
             :pen-down? true}
   :history []})

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

(defn backward
  [turtle distance]
  (forward turtle (* -1 distance)))

(defn turn-left 
  [turtle degrees]
  (update-property turtle :bearing + degrees))

(defn turn-right
  [turtle degrees]
  (update-property turtle :bearing - degrees))

(defn pen-down
  [turtle]
  (set-property turtle :pen-down? true))

(defn pen-up
  [turtle]
  (set-property turtle :pen-down? false))

(defn state-sequence
  [{:keys [state history]}]
  (conj history state))

(defn go
  [turtle x y]
  (set-property turtle :position {:x x :y }))
