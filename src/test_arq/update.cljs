(ns test-arq.update
  (:require [test-arq.util :as util]
            [beicon.core :as rx]))

(defrecord Error [^js/Error err]
  util/EffectEvent
  (process-effect [{:keys [err]} model]
    (.log js/console err)))

(defrecord Log [^string log]
  util/EffectEvent
  (process-effect [{:keys [log]} model]
    (println ">>> LOG " log)))

(defrecord Refresh []
  util/UpdateEvent
  (process-update [_ model]
    model))

(defrecord Decrement [^number qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (when (< (:counter model) 1) (throw js/Error. "NO NEGATIVE NUMBERS"))
    (update model :counter (partial + (- qty))))

  util/EffectEvent
  (process-effect [{:keys [qty]} model]
    (.log js/console (str ">> QTY: " qty ", model: " model)))

  util/WatchEvent
  (process-watch [{:keys [qty]} model]
    (rx/from-coll [(Log. "Hello")
                   (Log. "Decrement")])))

(defrecord Increment [^number qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (update model :counter (partial + qty)))

  util/EffectEvent
  (process-effect [{:keys [qty]} model]
    (.log js/console (str ">> QTY: " qty ", model: " model)))

  util/WatchEvent
  (process-watch [{:keys [qty]} model]
    (rx/from-coll [(Log. "Hello")
                   (Log. "Increment")])))
