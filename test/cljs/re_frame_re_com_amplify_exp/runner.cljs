(ns re-frame-re-com-amplify-exp.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [re-frame-re-com-amplify-exp.core-test]))

(doo-tests 're-frame-re-com-amplify-exp.core-test)
