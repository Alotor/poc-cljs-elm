(ns test-arq.util
  (:require [beicon.core :as rx]))

;; Basic protocols for updating

;;; process-update :: Event -> Model -> Model
(defprotocol UpdateEvent
  (process-update [event model]))

(defn update? [e] (satisfies? UpdateEvent e))

;;; process-watch  :: Event -> Stream[Event]
(defprotocol WatchEvent
  (process-watch [event model]))

(defn watch? [e] (satisfies? WatchEvent e))

(defprotocol EffectEvent
  (process-effect [event model]))

(defn effect? [e] (satisfies? EffectEvent e))


;; Defines model stream
(defonce event-stream (rx/bus))

(defn signal [event]
  (fn [e]
    (rx/push! event-stream event)))

(defn model-changes-stream [event-stream init-model]
  (let [update-stream (->> event-stream (rx/filter update?))
        watch-stream  (->> event-stream (rx/filter watch?))
        effect-stream (->> event-stream (rx/filter effect?))
        model-stream  (->> model-changes
                           (rx/scan #(process-update %2 %1) init-model))]

    ;; Process effects: combine with the latest model to process the new effect
    (-> (rx/with-latest-from effect-stream model-stream vector)
        (rx/subscribe (fn [[event model]] (process-effect event model))))

    ;; Process event sources: combine with the latest model and the result will be
    ;; pushed to the event-stream bus
    (as-> (rx/with-latest-from effect-stream model-stream vector) $
          (rx/flat-map (fn [[event model]] (process-watch event model)) $)
          (rx/subscribe $ signal #(signal (Error. %))))

    model-stream))
