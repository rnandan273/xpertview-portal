(ns xpertview.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(reg-sub
  :loading-state
  (fn [db _]
    (:loading-state db)

    ))

 (reg-sub
 :login-status
 (fn [db]
    (:login-status db))) 

 (reg-sub
 :register-status
 (fn [db]
    (:register-status db))) 
