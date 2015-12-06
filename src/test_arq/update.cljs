(ns test-arq.update
  (:require [test-arq.util :as util]))

(defrecord Error [err]
  util/UpdateEvent
  (process-update [{:keys [err]} model]
    (.log js/console err)
    model))

(defrecord Refresh []
  util/UpdateEvent
  (process-update [_ model]
    model))

(defrecord Decrement [qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (update model :counter (partial + (- qty)))))

(defrecord Increment [qty]
  util/UpdateEvent
  (process-update [{:keys [qty]} model]
    (update model :counter (partial + qty))))
