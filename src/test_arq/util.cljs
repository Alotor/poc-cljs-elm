(ns test-arq.util
  (:require [beicon.core :as rx]))

;; Basic protocols for updating

;;; process-update :: Event -> Model -> Model
(defprotocol UpdateEvent
  (process-update [event app]))

(defn update-event? [e] (satisfies? UpdateEvent e))

;;; process-watch  :: Event -> Stream[Event]
(defprotocol SourceEvent
  (process-watch [event]))

(defn source-event? [e] (satisfies? SourceEvent e))


;; Defines model stream
(defonce event-stream (rx/bus))

(defn signal [event]
  (fn [e]
    (rx/push! event-stream event)))

(defn model-changes-stream [event-stream init-model]
  (let #_[source-changes (->> event-stream
                              (rx/filter source-event?)
                              (rx/flat-map process-watch))

          model-changes (rx/merge (->> event-stream
                                       (rx/filter update-event?))
                                  source-changes)]
       [model-changes (->> event-stream
                           (rx/filter update-event?))]
       (->> model-changes
            (rx/scan #(process-update %2 %1) init-model))))
