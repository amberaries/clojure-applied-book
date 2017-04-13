(ns shipping.reducer
  (:require [shipping.domain :as domain]
            [clojure.core.reducers :as r]))

(defn ground-weight [products]
  (->> products
       (r/filter domain/ground?)
       (r/map :weight)
       (r/fold +)))
