(ns xpertview.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[xpertview started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[xpertview has shut down successfully]=-"))
   :middleware identity})
