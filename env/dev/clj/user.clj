(ns user
  (:require [mount.core :as mount]
            [xpertview.figwheel :refer [start-fw stop-fw cljs]]
            xpertview.core))

(defn start []
  (mount/start-without #'xpertview.core/repl-server))

(defn stop []
  (mount/stop-except #'xpertview.core/repl-server))

(defn restart []
  (stop)
  (start))


