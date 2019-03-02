(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))


(def base-team-classes
  ["rounded-sm" "text-sm" "px-4" "py-2" "my-1"])

(def active-team-classes
  (concat
    base-team-classes
    ["hover:bg-grey" "bg-grey-light" "cursor-pointer"]))

(def inactive-team-classes
  (concat
    base-team-classes
    ["bg-grey-lightest" "text-grey"]))

(def pending-team-classes
  (concat
    base-team-classes
    ["bg-grey-lighter" "text-grey-lighter"]))

(defn- team-item [team matchup-info]
  (fn []
    (log team)
    [:li
     {:on-click #(dispatch [::events/select-team matchup-info])
      :class (if (true? (:selected team))
               inactive-team-classes
               active-team-classes)}
     (:name team)]))

(defn- empty-matchup []
  [:<>
   [:li {:class pending-team-classes} "."]
   [:li {:class pending-team-classes} "."]])

(defn- matchup
  [{:keys [teams round-index group-index group-height]
    :as matchup-info}]
  (fn []
    [:div.flex.flex-col.justify-center
     {:style {:height (str group-height "%")}}
     [:ul.list-reset
      (if (= :empty teams)
        [empty-matchup]
        (map-indexed
          (fn [team-index team]
            ^{:key team-index}
             [team-item team (assoc matchup-info
                                    :team-index team-index)])
          teams))]]))

(defn round [round-index]
  (let [round-teams (subscribe [::subs/bracket round-index])]
    (fn []
      (let [group-height (/ 100 (count @round-teams))]
        [:ul.list-reset.mx-3
         {:class "w-1/4"}
         (map-indexed
           (fn [group-index teams]
             (log "teams" teams)
             ^{:key (str group-index round-index teams)}
              [matchup {:teams (if (empty? teams) :empty teams)
                        :round-index round-index
                        :group-index group-index
                        :group-height group-height}])
           @round-teams)]))))

(defn render []
  [:div.flex
   [:div.flex.h-screen.relative.p-8
    {:class "w-1/2"}
    [round 0]
    [round 1]
    [round 2]
    [round 3]]])
