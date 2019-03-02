(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))

(defn- team-item [team matchup-info]
  (fn []
    [:li.bg-grey-light.m-2.p-4.cursor-pointer.rounded-sm
     {:on-click #(dispatch [::events/select-team matchup-info])
      :class (when (true? (:selected team))
                   "text-grey")}
     (:name team)]))

(defn- matchup [{:keys [teams round-index group-index]
                 :as matchup-info}]
  (fn []
    [:ul.list-reset.p-4
     (map-indexed
       (fn [team-index team]
         ^{:key team-index}
          [team-item team (assoc matchup-info
                                 :team-index team-index)])
       teams)]))

(defn round [round-index]
  (let [round-teams (subscribe [::subs/bracket round-index])]
    (fn []
      [:ul.list-reset
       (map-indexed
         (fn [group-index teams]
           ^{:key (str group-index round-index teams)}
            [matchup {:teams teams
                      :round-index round-index
                      :group-index group-index}])
         @round-teams)])))

(defn render []
  [:div.flex
   [round 0]
   [round 1]])
