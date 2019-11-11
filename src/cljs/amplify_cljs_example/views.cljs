
(ns amplify-cljs-example.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [clairvoyant.core :refer-macros [trace-forms]]
   [re-frame-tracer.core :refer [tracer]]
   [amplify-cljs-example.subs :as subs]
   [amplify-cljs-example.events :as events]
   [reagent.core   :as    reagent]
   ["aws-amplify" :default Amplify :as amp]
   ["aws-sdk" :as AWS]
   ))

(trace-forms {:tracer (tracer :color "gold")}

(defn title []
   (let [name (re-frame/subscribe [::subs/name])
         auth-state (re-frame/subscribe [::subs/auth-state])]

    [re-com/title
     :label (str "Hello from " @name " auth-state: " @auth-state)
     :level :level1]
    ))

;; (defn list-s3
;;   )

(defn main-panel []
  (js/console.log "main-panel")
  (js/console.log "amp/Auth: " amp/Auth)

  (let [aws-config "foo"]
    [re-com/v-box
     :height "100%"
     :children [[title]
                [re-com/v-box
                 :children [[re-com/button
                             :label            "Unleash the Listing"
                             :tooltip          "Click to fetch route53 list Hosted Zones"
                             :tooltip-position :below-center
                             :on-click          #(re-frame/dispatch [::events/fetch-route53-list] nil)
                             :class             "btn-danger"]
                            [re-com/box
                             :align :center      ;; note: centered text wrt the button
                             :child  [re-com/label
                                      :label (str @(re-frame/subscribe [::subs/route53-list]))
                                      :style {:margin-left "15px"}]]]]
                ]])))
