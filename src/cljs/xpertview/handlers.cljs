(ns xpertview.handlers
  (:require [re-frame.core :as rf]
            [xpertview.db :as db]
            [re-frame.core :refer [dispatch reg-event-db]]
            [cljs.core.async :as async :refer [chan close!]]
              [clojure.walk :as walk]
              [cognitect.transit :as t]
              [ajax.core :refer [GET POST]])
    (:require-macros
    [cljs.core.async.macros :refer [go alt!]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(defn log [s]
  (.log js/console (str s)))

(defn error-handler [ch response]
  (log response)
  (rf/dispatch [:end-download])
  (log "DONE"))

(defn read-register-response [response]
  (let [kwresp (walk/keywordize-keys response)]
    (log (str "POST --->>>> " kwresp))
    (log (keys kwresp))
    (rf/dispatch [:register-complete (if (contains? kwresp :error) 1 0)])))

(defn read-login-response [response]
  (let [kwresp (walk/keywordize-keys response)]
    (log (str "POST --->>>> " kwresp))
    (log (keys kwresp))
    (rf/dispatch [:login-complete (if (contains? kwresp :error) 1 0)])))

(reg-event-db
  :start-download
 (fn [db [_ _]]
   ;(assoc db :loading-state "loading")
   (assoc db :loading-state true)

   ))

(reg-event-db
  :end-download
 (fn [db [_ _]]
  ; (assoc db :loading-state "hide")
   (assoc db :loading-state false)
   ))

(reg-event-db
 :register-complete
 (fn [db [_ msg]] 
  (rf/dispatch [:end-download])
  (assoc db :register-status msg)))

  (reg-event-db
 :login-complete
 (fn [db [_ msg]] 
  (rf/dispatch [:end-download])
  (assoc db :login-status msg)))

(defn response-handler [ch response]
  (go (>! ch response)(close! ch))
  (log "DONE"))

(defn do-http-post [url doc]
  (log "POSTING ---->")
  (log (str "POST " url (clj->js doc)))
  (let [ch (chan 1)]
    (POST url {:params  (clj->js doc) :format :json :handler (fn [response] (response-handler ch response))
               :error-handler (fn [response] (response-handler ch response))})
    ch)
  )

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
  :register-feedback
 (fn [db [_ register-doc]]
   (log (str "Your register documnents " register-doc))
   (go
     (rf/dispatch [:start-download])
     (read-register-response (<! (do-http-post "/user_register" register-doc))))
     db))

(reg-event-db
  :login-feedback
 (fn [db [_ login-doc]]
   (log (str "Your login documnents " login-doc))
   (go
     (rf/dispatch [:start-download])
     (read-login-response (<! (do-http-post "/user_login" login-doc))))
     db))

