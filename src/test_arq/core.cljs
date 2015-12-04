;; POC based on the ELM architecture
;; > https://github.com/evancz/elm-architecture-tutorial/
(ns test-arq.core
  (:require [rum.core :as rum :refer-macros [defc defcs]]
            [beicon.core :as rx]))

(enable-console-print!)

;; ------ MODEL -------

(defn init-model []
  {:counter 0})

;; ------ UPDATE ------
(defrecord Refresh [])
(defrecord Decrement [qty])
(defrecord Increment [qty])

;;; process-update :: Event -> Model -> Model
(defprotocol UpdateEvent
  (process-update [event app]))

(defn update-event? [e] (satisfies? UpdateEvent e))

;;; process-watch  :: Event -> Stream[Event]
(defprotocol SourceEvent
  (process-watch [event]))

(defn source-event? [e] (satisfies? SourceEvent e))

(extend-protocol UpdateEvent
  Refresh
  (process-update [_ app]
    app)

  Decrement
  (process-update [{:keys [qty]} app]
    (update app :counter #(- % qty)))

  Increment
  (process-update [{:keys [qty]} app]
    (update app :counter #(+ % qty))))


;; ----- VIEW -----
(defc label [signal app]
  [:div
   [:h1 "Simple Counter"]
   [:div (:counter app)]
   [:div
    [:button {:on-click (signal (Decrement. 10))} "Decrement 10"]
    [:button {:on-click (signal (Decrement. 1))}  "Decrement 1"]
    [:button {:on-click (signal (Increment. 1))}  "Increment 1"]
    [:button {:on-click (signal (Increment. 10))} "Increment 10"]
    ]])


;; -------- CONNECT EVERYTHING ---------
(defn model-stream [event-stream model]
  (let #_[source-changes (->> event-stream
                              (rx/filter source-event?)
                              (rx/flat-map process-watch))

          model-changes (rx/merge (->> event-stream
                                       (rx/filter update-event?))
                                  source-changes)]
       [model-changes (->> event-stream
                           (rx/filter update-event?))]
       (->> model-changes
            (rx/scan #(process-update %2 %1)
                     model))))

(defonce event-stream (rx/bus))

(defn signal [event]
  (fn [e]
    (rx/push! event-stream event)))

(defn init-app [root model-constructor]
  (let [model (model-constructor)
        node (.getElementById js/document "app")
        component (rum/mount (root signal model) node)]
    (-> event-stream
        (model-stream model)
        (rx/subscribe #(rum/mount (root signal %) node)
                      #(.log js/console "error: " %)
                      #(.log js/console "end: " %)))))

(init-app label init-model)

(defn on-js-reload []
  (rx/push! event-stream (->Refresh))
)
