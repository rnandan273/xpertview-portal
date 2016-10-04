(ns xpertview.core
  (:require [cljsjs.material-ui]
            [cljs-react-material-ui.core :as ui]
            [cljs-react-material-ui.reagent :as rui]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [xpertview.ajax :refer [load-interceptors!]]
            [xpertview.handlers]
            [xpertview.subscriptions])
  (:import goog.History))

(def app-state (r/atom {:dialog-state false :login-dialog-state false }))


(defn log [s]
  (.log js/console (str s)))

(def styles {
  :div {
    :display "flex" :flexDirection "column" :flexFlow "column wrap" :padding 20 :backgroundcolor "blue200" :width "100%"},
  :div2 {
    :display "flex" :flexDirection "row" :flexFlow "row wrap" :padding-top 20 :backgroundcolor "blue200" :width "100%"},
  :tool-bar {
    :display "flex" :flexDirection "row" :flexFlow "row wrap" :backgroundcolor "blue200" :width "100%"}
  })

(def unselected-text-color "#3E2723")
(def selected-text-color "#00C4C4")
;(def title-bar-bg-color "#E3FDFD")
(def title-bar-bg-color "WHITE")

(def product-like-color "#FF6161")
(def note-color "#FF6161")
(def product-dislike-color "#DCDEE0")
(def product-bg-color "#DCDEE0")


(defn login-page []
 (let [login-doc (r/atom {:email "" :username "" :password "" :confirm-password ""})
       login-status (rf/subscribe [:login-status])]
 (fn []
   (log (str "Login PAGE : " @login-status))
   [:div {:style {:display "flex" :flex-direction "column" :flex-flow "column wrap"}}
     [:div {:style {:flex 1 :align-self "center"}} [:h3 {:style {:font-family "avenir" :color note-color}} (if (= 0 @login-status) (str "Your login is successful ") (str "Login With Us"))]
      ]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "E-mail"
                           :floating-label-text "E-mail"
                           :on-change #(swap! login-doc assoc :email (.. % -target -value))}]]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "Password"
                           :floating-label-text "Password"
                           :type "password"
                           :on-change #(swap! login-doc assoc :password (.. % -target -value))}]]

     [:div {:style {:flex 1 :align-self "center"}}
           [:div {:style {:display "flex" :flex-direction "row" :flex-flow "row wrap"}}
           [:div {:style {:flex 1}}
            [ui/flat-button {:key "contactus"
                             :on-touch-tap #(rf/dispatch [:login-feedback @login-doc])
                             :background-color "#FF6161"
                             :label "Submit"}]]

           ]]])))

(defn register-page []
 (let [register-doc (r/atom {:email "" :username "" :password "" :confirm-password ""})
       register-status (rf/subscribe [:register-status])]
 (fn []
   (log (str "Register PAGE : " @register-status))
   [:div {:style {:display "flex" :flex-direction "column" :flex-flow "column wrap"}}
     [:div {:style {:flex 1 :align-self "center"}} [:h3 {:style {:font-family "avenir" :color note-color}} (if (= 0 @register-status) (str "Your registration is successful ") (str "Register With Us"))]
      ]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "Name"
                           :floating-label-text "Name"
                           :on-change #(swap! register-doc assoc :username (.. % -target -value))
                                   }]]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "E-mail"
                           :floating-label-text "E-mail"
                           :on-change #(swap! register-doc assoc :email (.. % -target -value))
                                   }]]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "Password"
                           :floating-label-text "Password"
                           :type "password"
                           :on-change #(swap! register-doc assoc :password (.. % -target -value))
                                   }]]

     [:div {:style {:flex 1 :align-self "center"}}
          [rui/text-field {:style {:font-family "avenir" :color note-color}
                           :hintText "Re-enter password "
                           :type "password"
                           :floating-label-text "Re-enter password"
                           :on-change #(swap! register-doc assoc :confirm-password (.. % -target -value))
                                   }]]

     [:div {:style {:flex 1 :align-self "center"}}
           [:div {:style {:display "flex" :flex-direction "row" :flex-flow "row wrap"}}
           [:div {:style {:flex 1}}
            [ui/flat-button {:key "contactus"
                             :on-touch-tap #(rf/dispatch [:register-feedback @register-doc])
                             :background-color "#FF6161"
                             :label "Submit"}]]

           ]]])))

(defn get-tagged-text [txt]
  [:h4 {:style {:font-family "avenir" :color unselected-text-color}} txt])

(defn get-highlighted-tagged-text [txt]
  [:h4 {:style {:font-family "avenir" :color selected-text-color}} txt])

(defn title-bar [active-page]
  (fn []
  [rui/mui-theme-provider
          {:mui-theme (ui/get-mui-theme {:palette {:text-color (ui/color :brown900) :canvas-color title-bar-bg-color}})}
    [:div
    [:div {:class "mynav"}
       [rui/paper {:zDepth 0 :style (:div2 styles)}
         [:div {:style {:flex 8}} [:span]]

        [:div {:style {:flex 1 :cursor "pointer"}} [:a {:href "#/"} (get-tagged-text "Home")]]
        [:div {:style {:flex 1 :cursor "pointer"}} [:a {:href "#/about"} (get-tagged-text "About")]]

        [:div {:style {:flex 1 :cursor "pointer"} :on-click #(swap! app-state assoc-in [:login-dialog-state] true)} (get-tagged-text "Login")
                [rui/dialog
                            { :modal false
                              :open (:login-dialog-state @app-state)
                              :on-request-close #(swap! app-state assoc-in [:login-dialog-state] false)
                              :auto-scroll-body-content true
                              :content-style {:width "90%" :max-width "1000px" }
                             }
                             [:div {:style {
                                :display "flex"
                                :align-items "center"
                                :flex-direction "row"
                                :float "right"}}
                                  [:div {:style {:flex 4 }}
                                     [ui/flat-button {:key "Done"

                                                     :icon         (ic/navigation-close)
                                                     :on-touch-tap #(swap! app-state assoc-in [:login-dialog-state] false)}]]]

              [rui/mui-theme-provider {:mui-theme (ui/get-mui-theme {:palette {:canvas-color "white" :text-color "black"}})}
                  [:div {:style {:display "flex" :flex-flow "column wrap"}}
                  [:div {:style {:flex 1 :align-self "center"}} 
                    [:div {:style {:flex 0.1}} 
                         [:a {:href "/syncfb" } [:img {:class "ui centered medium image" :src "http://newl2mr.listen2myradio.com/img/facebook-login.png"}][:br]]]]
                  
                  [:div {:style {:flex 1}} 
                    [rui/tabs 
                       [rui/tab {:label "Login" :value "login"}[:div {:style {:flex 1}} [login-page]]]
                       [rui/tab {:label "Register" :value "register"}[:div {:style {:flex 1}} [register-page]]]
                       ]]]]]]

         ;[:div {:style {:flex 1 }} [rui/refresh-indicator {:size 150 :left 500 :top 50 :loadingColor "#FF9800" :status @(rf/subscribe [:loading-state])}]]
         [:div {:style {:flex 1}}
               [rui/dialog
                           { :modal false
                             :open @(rf/subscribe [:loading-state])
                            }
                            [rui/linear-progress {:mode "indeterminate"}]]
         ]]]


         (comment

              [:div {:class "mynavmob"}
               [rui/paper {:zDepth 1 :style (:div2 styles)}

                 [:div {:style {:flex 1 }} [:a {:href "#/" } [:img {:height "24px" :width "74px" :src "/img/grey_logo.png"}]]]
                 [:div {:style {:flex 0.1}}
                    [rui/icon-menu {:icon-button-element (ic/navigation-menu)}
                      [rui/menu-item {:primary-text "Home" :href "#/" :on-touch-tap #(log "HOME")}]
                      [rui/menu-item {:primary-text "Inspirations" :href "#/inspirations" :on-touch-tap #(log "BLOG")}]
                      [rui/menu-item {:primary-text "Curated Discovery" :href "#/discover" :on-touch-tap #(log "BLOG")}]
                      [rui/menu-item {:primary-text "Favorites" :href "#/favourites" :on-touch-tap #(log "BLOG")}]]]]]
              )
         ]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of xpertview... work in progress"]]])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to xpertview"]]
   [:div.row
    [:div.col-md-12
     [:h2 "Coming Soon"]]]])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn get-style [active-page]
    (log active-page)
    (if (= :home active-page)
      {:display "flex" :padding "1px" :flex-flow "column wrap"  :background-color "white"
      :background-size "cover" :background-position "center" :background-repeat "no-repeat"}
      {:display "flex" :padding "1px" :flex-flow "column wrap"}
    ))


(defn page []
  (let [active-page (rf/subscribe [:page])]
    (fn []
      (log (get-style @active-page))
      [rui/mui-theme-provider
          {:mui-theme (ui/get-mui-theme {:palette {:canvas-color "white" :text-color product-bg-color}})}
          [:div {:style {:display "flex" :padding "1px" :flex-flow "column wrap"}}
              [:div {:style {:flex 1}}
               [:div {:style (get-style @active-page)}
                  [:div {:style {:flex 1}}
                      [:div {:style {:display "flex" :padding "1px" :flex-flow "column wrap"}}
                        [:div {:style {:flex 1}} [(title-bar @active-page)]]
                        [:div {:style {:flex 1}} [(pages @active-page)] [:br]]]]]]
                ]])))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (log "home")
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/about" []
  (log "About")
  (rf/dispatch [:set-active-page :about]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
