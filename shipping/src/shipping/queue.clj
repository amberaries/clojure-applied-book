(ns shipping.queue)

(defn queue
  []
  (ref clojure.lang.PersistentQueue/EMPTY))

(defn enq
  [q item]
  (dosync
    alter q conj item))

(defn deq
  [q]
  (dosync
    (let [item (peek @q)]
      (alter q pop)
      item)))
