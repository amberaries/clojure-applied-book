(ns shipping.stat)

(def pageview-stat (agent 0))

(defn remote-send
  [key new-val]
  ,,,)

(add-watch
  pageview-stat
  :pageview
  (fn [key agent old new]
    (when (zero? (mod new 10))
      (remote-send key new))))

(defn inc-stat
  [stat]
  (send-off stat inc))
