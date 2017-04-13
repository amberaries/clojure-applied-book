(ns shipping.seq
  (:require [shipping.domain :refer :all]))

(defn ground-weight [products]
  (->> products
       (filter ground?)
       (map :weight)
       (reduce +)))
