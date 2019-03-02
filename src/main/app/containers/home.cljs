(ns app.containers.home
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [reagent.core :as reagent]
            [app.utils :as utils]
            [app.subs :as subs]
            [app.events :as events]
            [app.history :as history]))

(defn render []
  [:p "Hello"])
