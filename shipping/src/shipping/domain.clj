(ns shipping.domain)

(def classes [:ground :air :overnight])

(defn ground? [product]
  (= :ground (:class product)))
