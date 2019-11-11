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

(defn make-config [creds]
  (let [config (AWS/Config. (clj->js {:credentials creds :region "us-east-1"}))]
    (js/console.log "make-config creds:  " creds " config: " config)
    (println "println make-config creds:  " creds " config: " config)
    config))

(trace-forms {:tracer (tracer :color "green")}

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced initialize-db-handler [_ _]
   db/default-db))

(re-frame/reg-fx
 :gen-aws-config
 (fn-traced gen-aws-config-handler [aws-creds]
            (println "gen-aws-config-handler aws-creds: " aws-creds)
            (println "gen-aws-config-handler config: " (make-config aws-creds))))

(re-frame/reg-fx
 :gen-service-objects
 (fn-traced gen-service-objects-handler [aws-creds]
            (println "gen-service-objects-handler")
            (re-frame/dispatch [:set-service-objects {:route53 (new AWS/Route53 (:service-object-credentials aws-creds))}])))

(re-frame/reg-event-db
 :set-service-objects
 (fn-traced set-service-objects-handler [db [_ new-object]]
            (assoc-in db [:service-objects]  new-object)))

(re-frame/reg-fx
 :setup-fetch-route53-list
 (fn setup-fetch-route53-list [_]
            (let [route53 @(re-frame/subscribe [:service-objects])]
              (route53.listHostedZones (clj->js {}) (fn listHostedZones-handler [err data]
                                                      (if err
                                                        (js/console.log err err.stack)
                                                        (js/console.log "success: " data)))))))

(re-frame/reg-event-fx
 ::fetch-route53-list
 (fn-traced fetch-route53-list [_ [_ _]]
            (println "::fetch-route53-list")
            {:setup-fetch-route53-list nil}))

(re-frame/reg-event-fx
 ::set-aws-creds
 (fn-traced set-aws-creds-handler [{:keys [db]} [_ aws-creds]]
            (println "::set-aws-creds-handler-fx " aws-creds)
            {:db (assoc-in db [:creds :aws-access] aws-creds)
             :gen-aws-config aws-creds
             :gen-service-objects aws-creds}))

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
                                     :secretAccessKey (.-secretAccessKey response)
                                     :service-object-credentials (amp/Auth.essentialCredentials response)}]))))))
)
