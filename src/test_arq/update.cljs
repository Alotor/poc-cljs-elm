(ns test-arq.update
  (:require [test-arq.util :as util]))

(defrecord Error [^js/Error err]
  util/EffectEvent
  (process-effect [{:keys [err]} model]
    (.log js/console err))

  util/UpdateEvent
  (process-update [event model]
    ()))

(defrecord Refresh []
  util/UpdateEvent
  (process-update [_ model]
    model))

(defrecord Decrement [^number qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (update model :counter (partial + (- qty)))))

(defrecord Increment [^number qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (update model :counter (partial + qty))))
