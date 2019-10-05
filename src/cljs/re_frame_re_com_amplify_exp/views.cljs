(ns re-frame-re-com-amplify-exp.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [re-frame-re-com-amplify-exp.subs :as subs]
   ))

(defn title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :label (str "Hello from " @name)
     :level :level1]))

(defn main-panel []
  [re-com/v-box
   :height "100%"
   :children [[title]
              ]])
