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

(defn push-history
  [{:keys [state] :as turtle}]
  (update-in turtle [:history] conj state))

(defn move-forward
  [{{:keys [bearing]} :state :as turtle} distance]
  (-> turtle
    push-history
    (->/in [:state :position]
           (update-in [:x] + (* distance (Math/cos bearing)))
           (update-in [:y] + (* distance (Math/sin bearing))))))

(defn- update-bearing
  [turtle f degrees]
  (-> turtle
    push-history
    (update-in [:state :bearing] f (degrees->rad degrees))))

(defn turn-left 
  [turtle degrees]
  (update-bearing turtle + degrees))

(defn turn-right
  [turtle degrees]
  (update-bearing turtle - degrees))

(defn- set-pen-state
  [turtle down?]
  (-> turtle
    push-history
    (assoc-in [:state :pen-down?] down?)))

(defn lower-pen
  [turtle]
  (set-pen-state turtle true))

(defn raise-pen
  [turtle]
  (set-pen-state turtle false))

(defn state-sequence
  [{:keys [state history]}]
  (conj history state))
