(ns amplify-cljs-example.events
  (:require
   [re-frame.core :as re-frame]
   [amplify-cljs-example.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [clairvoyant.core :refer-macros [trace-forms]]
   [re-frame-tracer.core :refer [tracer]]
   ["aws-amplify" :refer (Hub) :default Amplify :as amp]
   ["aws-sdk" :as AWS]
   ))

(trace-forms {:tracer (tracer :color "green")}

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced initialize-db-handler [_ _]
   db/default-db))

(defn make-config [creds]
  (let [config (AWS/Config. (clj->js {:credentials creds :region "us-east-1"}))]
    (js/console.log "make-config creds:  " creds " config: " config)
    (println "println make-config creds:  " creds " config: " config)
    config))

(re-frame/reg-event-fx
 ::set-aws-config
 (fn-traced set-aws-config-handler [{:keys [db]} [_ aws-creds]]
            (println "set-aws-config-handler aws-creds: " aws-creds)
            {:db (assoc-in db [:creds :aws-config] (make-config aws-creds))}))

;; Aws IAM creds based on the logged in user
;; Call only by the ::fetch-aws-current-creds fx
(re-frame/reg-event-db
 ::set-aws-creds
 (fn-traced set-aws-creds-handler [db [_ aws-creds]]
            (println "::set-aws-creds-handler " aws-creds)
            (re-frame/dispatch [::set-aws-config aws-creds])
            (assoc-in db [:creds :aws-access] aws-creds)))
            ;; {:db (assoc-in db [:creds :aws-access] aws-creds)
            ;;  ::set-aws-config aws-creds}))

(re-frame/reg-event-fx
 ::set-auth-state
 (fn set-auth-state-handler [{:keys [db]} [_ auth-state]]
   (js/console.log "::set-auth-state logged " auth-state)
   (let [base-resp {:db (assoc-in db [:auth-state]  auth-state)}]
     (if (= auth-state "signIn")
       (merge base-resp {:fetch-aws-current-creds nil})
       base-resp))))

(re-frame/reg-fx
 :setup-hub-listener
 (fn setup-hub-listener-handler []
   (js/console.log "Top of setup-hub-listener fx")
   (.listen Hub "auth"
            (fn hub-handler [data]
              (js/console.log "HUB payload event: " (.-event (.-payload data)))
              (re-frame/dispatch [::set-auth-state (.-event (.-payload data))])
              {}))))


(re-frame/reg-event-fx
 ::init-hub-listener
 (fn-traced init-hub-listener-handler [coeffect _]
   (js/console.log "Inside setup-hub-listener event-fx")
   {:setup-hub-listener nil}))

(re-frame/reg-fx
 :fetch-aws-current-creds
 (fn fetch-aws-current-creds-handler []
   (-> (amp/Auth.currentCredentials)
       (.then (fn current-credentials-handler [response]
                (re-frame/dispatch [::set-aws-creds
                                    {:accessKeyId (.-accessKeyId  response)
                                     :secretAccessKey (.-secretAccessKey response)}]))))))
)
