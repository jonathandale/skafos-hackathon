(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))


(def base-team-classes
  ["rounded-sm" "text-sm" "px-2" "py-1" "my-1"])

(def active-team-classes
  (concat
    base-team-classes
    ["hover:bg-grey-dark" "bg-grey-darker" "cursor-pointer"
     "text-white"]))

(def inactive-team-classes
  (concat
    base-team-classes
    ["bg-grey-light" "text-grey-dark"]))

(def pending-team-classes
  (concat
    base-team-classes
    ["bg-grey-lighter" "text-grey-lighter"]))

(defn- team-item [team matchup-info]
  (fn []
    (let [disabled? (some :selected (:teams matchup-info))]
      [:li
       {:on-click #(when-not disabled?
                     (dispatch [::events/select-team matchup-info]))
        :class (if disabled?
                 inactive-team-classes
                 active-team-classes)}
       [:p.text-xs
        [:span (:name team)]
        [:span.opacity-50 (str " ("(:ranking team)")")]]])))

(defn- empty-team []
  [:li {:class pending-team-classes} "."])

(defn- matchup
  [{:keys [teams round-index group-index group-height]
    :as matchup-info}]
  (fn []
    [:div.flex.flex-col.justify-center
     {:style {:height (str group-height "%")}}
     [:ul.list-reset
      (if-let [t1 (first teams)]
        [team-item t1 (assoc matchup-info :team-index 0)]
        [empty-team])
      (if-let [t2 (second teams)]
        [team-item t2 (assoc matchup-info :team-index 1)]
        [empty-team])]]))

(defn round [round-index]
  (let [round-teams (subscribe [::subs/bracket round-index])]
    (fn []
      (let [group-height (/ 100 (count @round-teams))]
        [:ul.list-reset.mr-2
         {:class "w-1/4"}
         (map-indexed
           (fn [group-index teams]
             ^{:key (str group-index round-index teams)}
              [matchup {:teams teams
                        :round-index round-index
                        :group-index group-index
                        :group-height group-height}])
           @round-teams)]))))

(defn render []
  [:div.flex
   [:div.flex.h-screen.relative.px-6
    {:class "w-1/2"}
    [round 0]
    [round 1]
    [round 2]
    [round 3]]])
