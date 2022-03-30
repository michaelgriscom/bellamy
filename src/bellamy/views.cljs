(ns bellamy.views
  (:require
   [re-frame.core :as re-frame]
   [bellamy.subs :as subs]))

(defn main-panel []
  [:div
   [:h1
    "Hello world"]])