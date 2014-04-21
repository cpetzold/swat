(ns swat.core
  (:use plumbing.core cosmo.schemas)
  (:require
   [schema.macros :as sm]
   [schema.core :as s]
   [cosmo.core :as cosmo]))

(sm/defschema Swatter
  {:atlas-path s/Str
   :region-name s/Str
   :size Size
   :position Point})

(sm/defn center :- Float
  [length :- Integer
   point :- Float]
  (- point (/ length 2)))

(sm/defn center-pos :- Point
  [size :- Size
   pos :- Point]
  (vec (map center size pos)))

(sm/defn add-texture-region
  [entity
   atlas-path :- s/Str
   region-name :- s/Str]
  (assoc entity
    :atlas-path atlas-path
    :region-name region-name))

(sm/defn add-bounding-box
  [entity
   size :- Size
   position :- Point]
  (assoc entity
    :size size
    :position (center-pos size position)))

(sm/defn create-swatter :- Swatter []
  (-> {}
      (add-texture-region "spritesheet.txt" "wire-swatter")
      (add-bounding-box [32 98] [0 0])))

(sm/defn init-state
  [size :- Size]
  {:size size
   :entities
   {:swatter (create-swatter)}})

(sm/defn update-swatter :- Swatter
  [swatter :- Swatter
   [_ win-height] :- Size]
  (let [mouse-pos (cosmo/mouse-pos)
        half-size (map #(/ % 2) (:size swatter))]
    (-> swatter
        (assoc :pos (vec (map - mouse-pos half-size)))
        (update-in [:pos 1] #(- win-height %)))))

(defn update [state dt]
  (update-in state [:entities :swatter] update-swatter (:size state)))

(defn init [state]
  (cosmo/set-cursor-catched true))

(sm/defn create
  [size :- Size]
  (cosmo/create
   {:title "Swat"
    :size size
    :init init
    :update update
    :clear-color [255 255 255]}
   (init-state size)))

(comment

  (def game (create [800 600]))

  )
