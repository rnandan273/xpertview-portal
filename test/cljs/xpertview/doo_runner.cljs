(ns xpertview.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [xpertview.core-test]))

(doo-tests 'xpertview.core-test)

