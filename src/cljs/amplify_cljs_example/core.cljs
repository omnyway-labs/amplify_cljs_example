(ns amplify-cljs-example.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [amplify-cljs-example.events :as events]
   [amplify-cljs-example.views :as views]
   [amplify-cljs-example.config :as config]
   ["aws-amplify" :refer (Hub) :default Amplify]
   ["aws-amplify-react" :refer (withAuthenticator)]
   ["/aws-exports.js" :default aws-exports]
   ))

(defn setup-hub-listener []
  (js/console.log "Top of setup-hub-listener")
  (re-frame/dispatch-sync [::events/init-hub-listener]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def root-view
  (reagent/adapt-react-class
   (withAuthenticator
    (reagent/reactify-component views/main-panel) true)))

(def aws-manual

  ;; User pool reframerecomamplifye16cd456a_userpool_16cd456a-dev
  {:Auth {:identityPoolId "us-east-1:61fe6ded-9c0f-481d-b32b-b624ad8119dc"
          :region "us-east-1"
          :userPoolId "us-east-1_GdM23KoC7"
          :userPoolWebClientId "3dbbodfcli1spu0junujjorjo6"
          }})

(defn ^:dev/after-load mount-root []
  (js/console.log "Top of mount-root")
  (re-frame/clear-subscription-cache!)

  (goog-define MANUAL false)

  (if MANUAL
    (.configure Amplify (clj->js aws-manual))
    (.configure Amplify aws-exports))

  (setup-hub-listener)
  (js/console.log "before render root-view")
  (reagent/render [root-view]
                  (.getElementById js/document "app")))

(defn init []
  (js/console.log "Top of init")
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (js/console.log "Before mount-root")
  (mount-root))
