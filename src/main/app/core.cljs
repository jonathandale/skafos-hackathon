(ns app.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [app.events :as events]
            [app.subs :as subs]
            [app.views :as views]
            [app.routes :as routes]
            [app.utils :as utils]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/app-root]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch [::events/set-window-dimensions (utils/window-dimensions)])
  (mount-root))
