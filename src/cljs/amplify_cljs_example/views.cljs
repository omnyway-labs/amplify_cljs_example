
(ns amplify-cljs-example.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [amplify-cljs-example.subs :as subs]
   ["aws-amplify" :default Amplify :as amp]
   ))

;; -------- Dispatchers
(re-frame/reg-event-db
 ::set-aws-creds
 (fn [db [_ aws-creds]]
   (assoc-in db [:creds :aws-access] aws-creds)))

(defn title []
  (let [name (re-frame/subscribe [::subs/name])]

    [re-com/title
     :label (str "Hello from " @name)
     :level :level1]
    ))

(defn main-panel []
  (-> (amp/Auth.currentCredentials)
      (.then (fn [response]
               (re-frame/dispatch [::set-aws-creds
                                   {:accessKeyId (.-accessKeyId  response)
                                    :secretAccessKey (.-secretAccessKey response)}]))))

  [re-com/v-box
   :height "100%"
   :children [[title]
              ]])
