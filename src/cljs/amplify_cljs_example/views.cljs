
(ns amplify-cljs-example.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [amplify-cljs-example.subs :as subs]
   [amplify-cljs-example.events :as events]
   ["aws-amplify" :default Amplify :as amp]
   ["aws-sdk" :as AWS]
   ))

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

  ;; (-> (amp/Auth.currentCredentials)
  ;;     (.then (fn [response]
  ;;              (re-frame/dispatch [::events/set-aws-creds
  ;;                                  {:accessKeyId (.-accessKeyId  response)
  ;;                                   :secretAccessKey (.-secretAccessKey response)}]))))

  (let [aws-confg @(re-frame/subscribe [::subs/aws-config])]
    [re-com/v-box
     :height "100%"
     :children [[title]
                [re-com/v-box
                 :children [[re-com/p ]]]
                ]]))
