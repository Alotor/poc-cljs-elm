(ns test-arq.view
  (:require [test-arq.update :as update]
            [rum.core :as rum :refer-macros [defc defcs]]))

(defc root [signal model]
  [:div
   [:h1 "Simple Counter"]
   [:div (:counter model)]
   [:div
    [:button {:on-click (signal (update/Decrement. 10))} "Decrement 10"]
    [:button {:on-click (signal (update/Decrement. 1))}  "Decrement 1"]
    [:button {:on-click (signal (update/Increment. 1))}  "Increment 1"]
    [:button {:on-click (signal (update/Increment. 10))} "Increment 10"]]])
