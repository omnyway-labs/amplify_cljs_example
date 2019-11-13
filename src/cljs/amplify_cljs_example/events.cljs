(ns amplify-cljs-example.events
  (:require
   [re-frame.core :as re-frame]
   [amplify-cljs-example.db :as db]
   [amplify-cljs-example.subs :as subs]
;   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [clairvoyant.core :refer-macros [trace-forms]]
   [re-frame-tracer.core :refer [tracer]]
   ["aws-amplify" :refer (Hub) :default Amplify :as amp]
   ["aws-sdk" :as AWS]
   ))

(trace-forms {:tracer (tracer :color "green")}

(defn make-config [creds]
  (let [config (AWS/Config. (clj->js {:credentials creds :region "us-east-1"}))]
    (js/console.log "make-config creds:  " creds " config: " config)
    (println "println make-config creds:  " creds " config: " config)
    config))

(re-frame/reg-event-db
 ::initialize-db
 (fn initialize-db-handler [_ _]
   db/default-db))

(re-frame/reg-fx
 :gen-aws-config
 (fn gen-aws-config-handler [aws-creds]
            (println "gen-aws-config-handler aws-creds: " aws-creds)
            (println "gen-aws-config-handler config: " (make-config aws-creds))))

(re-frame/reg-fx
 :gen-service-objects
 (fn gen-service-objects-handler [aws-creds]
            (println "gen-service-objects-handler")
            (re-frame/dispatch [:set-service-objects {:route53 (new AWS/Route53 (:service-object-credentials aws-creds))}])))

(re-frame/reg-event-db
 :set-service-objects
 (fn set-service-objects-handler [db [_ new-object]]
            (assoc-in db [:service-objects]  new-object)))

(re-frame/reg-event-db
 :set-route53-list
 (fn set-route53-list-handler [db [_ err data]]
            (js/console.log "set-route53-list-handler err: " err " data: " data)
            (println "(js->clj err :keywordize-keys true)): " (js->clj err :keywordize-keys true))
            (println "(js->clj data :keywordize-keys true): " (js->clj data :keywordize-keys true))
            (if err
              (assoc-in db [:route-53-list] (js->clj (js->clj err :keywordize-keys true)))
              (assoc-in db [:route-53-list] (js->clj (:HostedZones (js->clj data :keywordize-keys true)))))))

(re-frame/reg-fx
 :setup-fetch-route53-list
 (fn setup-fetch-route53-list [_]
   ;; Info on js externs type hints https://clojurescript.org/guides/externs
            (let [route53 ^js/AWS.Route53 (:route53 @(re-frame/subscribe [::subs/service-objects]))]
              (js/console.log "route53: " route53)
              (.listHostedZones route53  (clj->js {}) (fn listHostedZones-handler [err data]
                                                        (re-frame/dispatch [:set-route53-list err data]))))))

(re-frame/reg-event-fx
 ::fetch-route53-list
 (fn fetch-route53-list [_ [_ _]]
            (println "::fetch-route53-list")
            {:setup-fetch-route53-list nil}))

(re-frame/reg-event-fx
 ::set-aws-creds
 (fn set-aws-creds-handler [{:keys [db]} [_ aws-creds]]
            (println "::set-aws-creds-handler-fx " aws-creds)
            {:db (assoc-in db [:creds :aws-access] aws-creds)
             :gen-aws-config aws-creds
             :gen-service-objects aws-creds}))

(re-frame/reg-event-fx
 ::update-auth-state
 (fn update-auth-state-handler [{:keys [db]} [_ auth-state]]
   (js/console.log "::update-auth-state logged " auth-state)
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
              (re-frame/dispatch [::update-auth-state (.-event (.-payload data))])
              {}))))


(re-frame/reg-event-fx
 ::init-hub-listener
 (fn init-hub-listener-handler [coeffect _]
   (js/console.log "Inside setup-hub-listener event-fx")
   {:setup-hub-listener nil}))

(re-frame/reg-fx
 :fetch-aws-current-creds
 (fn fetch-aws-current-creds-handler []
            (js/console.log "fetch-aws-current-creds-handler")
            (-> (amp/Auth.currentCredentials)
                (.then (fn current-credentials-handler [response]
                         (js/console.log "current-credentials-handler")
                         (re-frame/dispatch [::set-aws-creds
                                             {:accessKeyId (.-accessKeyId  response)
                                              :secretAccessKey (.-secretAccessKey response)
                                              :service-object-credentials (amp/Auth.essentialCredentials response)}]))))))
)
