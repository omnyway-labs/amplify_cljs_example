(defproject amplify-cljs-example "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library]]
                 [thheller/shadow-cljs "2.8.68"]
                 [reagent "0.8.1"]
                 [re-frame "0.11.0-rc2"]
                 [re-com "2.6.0"]
                 [garden "1.3.9"]
                 [org.clojars.stumitchell/clairvoyant "0.2.1"]
                 [day8/re-frame-tracer "0.1.1-SNAPSHOT"]
                 [ns-tracker "0.4.0"]]

  :plugins [[lein-garden "0.3.0"]
            [lein-ancient "0.6.15"]]

  :min-lein-version "2.9.1"

  :jvm-opts ["-Xmx2G"]

  :source-paths ["src/clj" "src/cljs"]

  :test-paths   ["test/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css"]


  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   amplify-cljs-example.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :aliases {"dev"  ["with-profile" "dev" "run" "-m" "shadow.cljs.devtools.cli" "watch" "app"]
            "prod" ["with-profile" "prod" "run" "-m" "shadow.cljs.devtools.cli" "release" "app" "--config-merge" "{:closure-defines {re-frame.trace.trace-enabled? true day8.re-frame.tracing.trace-enabled? true clairvoyant.core.devmode true}}"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [day8.re-frame/re-frame-10x "0.4.5"]
                   [day8.re-frame/tracing "0.5.3"]]}

   :prod
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [day8.re-frame/re-frame-10x "0.4.5"]
                   [day8.re-frame/tracing "0.5.3"]]}

;   { :dependencies [[day8.re-frame/tracing-stubs "0.5.3"]]}
   })
