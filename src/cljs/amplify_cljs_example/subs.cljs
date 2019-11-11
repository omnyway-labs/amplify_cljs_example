(ns amplify-cljs-example.subs
  (:require
   [clairvoyant.core :refer-macros [trace-forms]]
   [re-frame-tracer.core :refer [tracer]]
   [re-frame.core :as re-frame]))

(trace-forms {:tracer (tracer :color "brown")}

(re-frame/reg-sub
 ::name
 (fn name-handler [db]
   (:name db)))

;; Aws IAM creds based on the logged in user
(re-frame/reg-sub
 ::aws-creds
 (fn aws-creds-handler [db _]
   (get-in db [:creds :aws-creds])))

(re-frame/reg-sub
 ::aws-config
 (fn aws-config-handler [db _]
   (get-in db [:creds :aws-config])))

(re-frame/reg-sub
 ::auth-state
 (fn auth-state-handler [db _]
   (get-in db [:auth-state])))

(re-frame/reg-sub
 ::service-objects
 (fn service-objects-handler [db _]
            (get-in db [:service-objects])))
)
