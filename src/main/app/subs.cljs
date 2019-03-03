(ns app.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub
  ::app-view
  (fn [db _]
    (:page db)))

(reg-sub
  ::matchup
  (fn [db [_ matchup]]))

(reg-sub
  ::bracket
  (fn [db [_ region round]]
    (nth (get-in db [:bracket region]) round)))
