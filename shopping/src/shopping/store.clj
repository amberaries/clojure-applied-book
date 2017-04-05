(ns shopping.store)

(def inventory
  (atom {}))

(declare sold-items)

(defn no-negative-values?
  [m]
  (not-any? neg? (vals m)))

(defn in-stock?
  [item]
  (let [cnt (item @inventory)]
    (and (pos? cnt))))

(defn init
  [items]
  (set-validator! inventory no-negative-values?)
  (reset! inventory items))

(defn grab
  [item]
  (if (in-stock? item)
    (swap! inventory update-in [item] dec)))

(defn stock
  [item]
  (swap! inventory update-in [item] inc))

(defn restock-order
  [k r ov nv]
  (doseq [item (for [kw (keys ov)
                     :when (not= (kw ov) (kw nv))] kw)]
    (swap! sold-items update-in [item] (fnil inc 0))
    (println "need to restock" item)))

(defn init-with-restock
  [m]
  (def inventory (atom m))
  (def sold-items (atom {}))
  (set-validator! inventory no-negative-values?)
  (add-watch inventory :restock restock-order))

(defn restock-all
  []
  (swap! inventory #(merge-with + % @sold-items))
  (reset! sold-items {}))
