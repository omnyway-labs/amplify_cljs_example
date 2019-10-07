(ns amplify-cljs-example.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [amplify-cljs-example.core-test]))

(doo-tests 'amplify-cljs-example.core-test)
