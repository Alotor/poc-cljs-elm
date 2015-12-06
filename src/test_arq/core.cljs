;; POC based on the ELM architecture
;; > https://github.com/evancz/elm-architecture-tutorial/

(ns test-arq.core
  (:require [test-arq.view :as view]
            [test-arq.model :as model]
            [test-arq.update :as update]
            [test-arq.util :as util]
            [rum.core :as rum]
            [beicon.core :as rx]))

(enable-console-print!)

(defn init-app [root-component model-constructor]
  (let [initial-model (model-constructor)
        node (.getElementById js/document "app")
        build-root (fn [model]
                     (rum/mount (root-component util/signal model) node))
        component (build-root initial-model)]

    (-> util/event-stream
        (util/model-changes-stream initial-model)
        #_(rx/retry))
        (rx/subscribe build-root
                      #(util/signal (update/Error. %))
                      #(.log js/console "end: " %))
        ))

(init-app view/root model/init)

(defn on-js-reload []
  (util/signal (update/Refresh.)))
