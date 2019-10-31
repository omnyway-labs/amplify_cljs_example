(ns amplify-cljs-example.events
  (:require
   [re-frame.core :as re-frame]
   [amplify-cljs-example.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   ["aws-sdk" :as AWS]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(defn make-config [creds]
  (let [config (AWS/Config. (clj->js {:credentials creds :region "us-east-1"}))]
    (js/console.log "creds:  " creds " config: " config)
    config))

(re-frame/reg-event-db
 ::set-aws-config
 (fn [db [_ aws-creds]]
   (js/console.log "::set-aws-config")
   (assoc-in db [:creds :aws-config] (make-config aws-creds))))

;; Aws IAM creds based on the logged in user
(re-frame/reg-event-db
 ::set-aws-creds
 (fn [db [_ aws-creds]]
   (js/console.log "::set-aws-creds")
   (re-frame/dispatch [::set-aws-config aws-creds])
   (assoc-in db [:creds :aws-access] aws-creds)
   ))
