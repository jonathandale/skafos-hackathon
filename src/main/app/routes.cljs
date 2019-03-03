(ns app.routes
  (:import goog.History)
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :as re-frame]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [app.events :as events]
            [app.history :as history]
            [pushy.core :as pushy]))

(secretary/set-config! :prefix "/")

(defn app-routes []
  (defroute "/" []
    (re-frame/dispatch [::events/set-page :bracket]))

  (pushy/start! history/history))
