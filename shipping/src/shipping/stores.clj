(ns shipping.stores)

(defn query
  [store product]
  ,,,)

(defn long-running-task
  []
  ,,,)

(defn query-stores
  [product stores]
  (let [futures (doall
                  (for [store stores]
                    (future (query store product))))]
    (map deref futures)))

(defn launch-timed
  []
  (let [begin-promise (promise)
        end-promise (promise)]
    (future (deliver begin-promise (System/currentTimeMillis))
            (long-running-task)
            (deliver end-promise (System/currentTimeMillis)))
    (println "Task started at " @begin-promise)
    (println "Task stopped at " @end-promise)))
