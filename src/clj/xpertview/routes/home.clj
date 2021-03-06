(ns xpertview.routes.home
  (:require [xpertview.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [org.httpkit.client :as http]
            [xpertview.ugutils :as ug]
            [taoensso.timbre :as timbre]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! alt!! timeout]])
  (:gen-class))

(defn home-page []
  (layout/render "home.html"))

(defn register-user [request]
  (let [user_data (or (get-in request [:params])
                      (get-in request [:body]))
       server-resp  (json/read-str (ug/register-user request) :key-fn keyword)]
   (timbre/info (str "You posted: " user_data))
   (timbre/info (str "UG response: " server-resp))
   server-resp 
   ))
  

(defn login-user [request]
  (let [user_data (or (get-in request [:params])
                      (get-in request [:body]))
        server-resp (json/read-str (ug/get-user-token request) :key-fn keyword)]
   (timbre/info (str "You posted: " user_data))
   
   {:status "true"}
   (timbre/info (str "UG response: " server-resp))
   server-resp))

(defroutes home-routes
  (GET "/" [] (home-page))
 (POST "/user_register" req 
      (timbre/info "register User")
      (response/ok (register-user req)))

(POST "/user_login" req 
      (timbre/info "Login User")
      (response/ok (login-user req)))
  (GET "/docs" [] (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                      (response/header "Content-Type" "text/plain; charset=utf-8"))))


