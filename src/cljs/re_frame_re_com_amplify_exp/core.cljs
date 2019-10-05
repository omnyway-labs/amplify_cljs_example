(ns re-frame-re-com-amplify-exp.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [re-frame-re-com-amplify-exp.events :as events]
   [re-frame-re-com-amplify-exp.views :as views]
   [re-frame-re-com-amplify-exp.config :as config]
   ["aws-amplify" :default Amplify :as amp]
   ["aws-amplify-react" :refer (withAuthenticator)]
   ["/aws-exports.js" :default aws-exports]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def root-view
  (reagent/adapt-react-class
   (withAuthenticator
    (reagent/reactify-component views/main-panel) true)))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (.configure Amplify aws-exports)
  (re-frame/dispatch-sync [::events/initialize-db])
  (reagent/render [root-view]
                  (.getElementById js/document "app")))

(defn init []
  (dev-setup)
  (mount-root))
