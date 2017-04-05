(ns shopping.family-async
  (:require [clojure.core.async :as async :refer [>!! >! <! go go-loop chan timeout]]
            [shopping.store :as store]))

(def my-kids #{:alice :bobbi :cindy :donnie})

(defn born!
  [new-kid]
  (alter-var-root #'my-kids conj new-kid))

(defn notify-parent
  [k r _ nv]
  (if (contains? nv :candy)
    (println "there's candy in the cart!")))

(def shopping-list (ref #{}))
(def assignments (ref {}))
(def shopping-cart (ref #{}))

(defn init
  []
  (store/init {:eggs 2 :bacon 3 :apples 3
               :candy 5 :soda 2 :milk 1
               :bread 3 :carrots 1 :potatoes 1
               :cheese 3})
  (dosync
    (ref-set shopping-list #{:milk :butter :bacon :eggs
                             :carrots :potatoes :cheese :apples})
    (ref-set assignments {})
    (ref-set shopping-cart #{})
    (add-watch shopping-cart :candy notify-parent)))

(defn maybe?
  [f]
  (if (= 0 (rand-int 3))
    (f)))

(defn collect-item-from-child
  [m child]
  (let [item (child  m)]
    (dissoc m child)
    (item)
    (assoc m :assigns (dissoc (:assigns m) child))
    (assoc m :cart (conj (:cart m) item))))

(defn assignment
  [child]
  (get @assignments child))

(defn buy-candy
  []
  (dosync
    (commute shopping-cart conj :candy)))

(defn collect-assignment
  [child]
  (let [item (assignment child)]
    (dosync
      (alter shopping-cart conj item)
      (alter assignments dissoc child)
      (ensure shopping-list))
    item))

(defn assign-item-to-child
  [child]
  (dosync
    (alter assignments assoc child (first @shopping-list))
    (alter shopping-list disj (assignment child)))
  (assignment child))

(defn send-child-for-item
  [child item q]
  (println child "is searching for" item)
  (maybe? buy-candy)
  (collect-assignment child)
  (>!! q child))

(defn report
  []
  (println "store inventory" @store/inventory)
  (println "shopping-list" @shopping-list)
  (println "assignments"   @assignments)
  (println "shopping-cart" @shopping-cart))

(defn go-shopping
  []
  (init)
  (report)
  (let [kids (chan 10)]
    (doseq [k my-kids]
      (>!! kids k))
    (go-loop [kid (<! kids)]
             (if (seq @shopping-list)
               (do
                 (go
                   (send-child-for-item kid (assign-item-to-child kid) kids))
                 (recur (<! kids)))
               (do
                 (println "done shopping.")
                 (report))))))
