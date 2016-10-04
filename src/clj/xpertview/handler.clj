(ns xpertview.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [xpertview.layout :refer [error-page]]
            [xpertview.routes.home :refer [home-routes]]
            [xpertview.routes.oauth :refer [oauth-routes]]
            [compojure.route :as route]
            [xpertview.env :refer [defaults]]
            [mount.core :as mount]
            [xpertview.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    #'oauth-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
