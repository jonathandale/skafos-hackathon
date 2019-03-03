(ns app.views
  (:require [app.routes :as routes]
            [app.subs :as subs]
            [re-frame.core :refer [subscribe]]
            [app.containers.bracket :as bracket]))

(defn page-view [content]
  [:div.antialiased.font-sans.font-medium
    content])

(defn app-view [page]
  [page-view
    (case page
      :bracket [app.containers.bracket/render]
      nil [:<>])])

(defn app-root []
  (app-view @(subscribe [::subs/app-view])))
