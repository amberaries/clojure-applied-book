(ns shipping.simple-chan
  (:require [clojure.core.async :refer (chan go <! <!! >!!)]
            [shipping.pipeline :as pl]))

(defn send-meassage
  [channel message]
  (>!! channel message))

(defn receive-message
  [channel]
  (go
    (loop []
      (when-some [val (<! channel)]
        (println val)
        (recur)))))

(let [c (chan 1)]
  (receive-message c)
  (send-meassage c "hello world!"))

(let [a (chan 1)
      b (chan 1)]
  (pl/assemble-stage a b)
  (receive-message b)
  (send-meassage a "Clojure is awesome but ruby is sad !"))
