(ns amplify-cljs-example.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

;; Aws IAM creds based on the logged in user
(re-frame/reg-sub
 ::aws-creds
 (fn [db _]
   (get-in db [:creds :aws-creds])))

(re-frame/reg-sub
 ::aws-config
 (fn [db _]
   (get-in db [:creds :aws-config])))

