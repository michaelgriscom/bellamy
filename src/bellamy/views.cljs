(ns bellamy.views
  (:require
   [re-frame.core :as re-frame]
   [bellamy.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello world"]
     ]))