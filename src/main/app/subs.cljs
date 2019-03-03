(ns app.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub
  ::app-view
  (fn [db _]
    (:page db)))

(reg-sub
  ::matchup
  (fn [db [_ [team-1 team-2]]]
    (get-in db [:matchups [(:id team-1) (:id team-2)]])))

(reg-sub
  ::bracket
  (fn [db [_ region round]]
    (nth (get-in db [:bracket region]) round)))

(reg-sub
  ::bracket-contender
  (fn [db [_ region]]
    (get-in db [:region-contender region])))

(reg-sub
  ::finalist
  (fn [db [_ position]]
    (get-in db [:finalists position])))

(reg-sub
  ::winner
  (fn [db [_]]
    (:winner db)))
