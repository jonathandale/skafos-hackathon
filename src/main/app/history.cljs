(ns app.history
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [pushy.core :as pushy]))

(def history (pushy/pushy secretary/dispatch!
                          #(when (secretary/locate-route %) %)))

(defn replace! [path]
  (pushy/replace-token! history path))
