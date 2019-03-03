(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))

(def base-team-classes
  ["rounded-sm" "text-sm" "px-2" "py-1" "m-1"])

(def active-team-classes
  (concat
    base-team-classes
    ["hover:bg-grey-darker" "bg-grey-darkest" "cursor-pointer"
     "text-white"]))

(def inactive-team-classes
  (concat
    base-team-classes
    ["opacity-50" "bg-grey"]))

(def pending-team-classes
  (concat
    base-team-classes
    ["bg-grey-lighter" "text-grey-lighter"]))

(defn- team-item [team matchup-info]
  (fn []
    (let [disabled? (some :selected (:teams matchup-info))]
      [:li.relative
       {:on-click #(when-not disabled?
                     (dispatch [::events/select-team matchup-info]))
        :class (if disabled?
                 inactive-team-classes
                 active-team-classes)}
       [:div.relative.z-10
        [:p.text-xs (:name team)]
        [:p.text-xs.opacity-75 "#" (:ranking team)]]
       [:div.absolute.pin-y.pin-l.bg-pink-dark
        {:style {:width "29%"
                 :opacity "0.8"}}]])))

(defn- empty-team []
  [:li {:class pending-team-classes}
   [:p.text-xs "."]
   [:p.text-xs "."]])

(defn- matchup
  [{:keys [teams round-index group-index group-height]
    :as matchup-info}]
  (fn []
    [:div.flex.flex-col.justify-center
     {:style {:height (str group-height "%")}}
     [:ul.list-reset.my-3
      (if-let [t1 (first teams)]
        [team-item t1 (assoc matchup-info :team-index 0)]
        [empty-team])
      (if-let [t2 (second teams)]
        [team-item t2 (assoc matchup-info :team-index 1)]
        [empty-team])]]))

(defn round [region round-index]
  (let [round-teams (subscribe [::subs/bracket region round-index])]
    (fn []
      (let [group-height (/ 100 (count @round-teams))]
        [:ul.list-reset.w-1:4
         (map-indexed
           (fn [group-index teams]
             ^{:key (str group-index round-index teams)}
              [matchup {:teams teams
                        :region region
                        :round-index round-index
                        :group-index group-index
                        :group-height group-height}])
           @round-teams)]))))

(defn render []
  [:div
   [:div.flex
    [:div.flex.w-4:9
     [round :east 0]
     [round :east 1]
     [round :east 2]
     [round :east 3]]
    [:div.flex.w-1:9]
    [:div.flex.w-4:9
     [round :west 3]
     [round :west 2]
     [round :west 1]
     [round :west 0]]]
   [:div.flex
    [:div.flex.w-4:9
     [round :midwest 0]
     [round :midwest 1]
     [round :midwest 2]
     [round :midwest 3]]
    [:div.flex.w-1:9]
    [:div.flex.w-4:9
     [round :south 3]
     [round :south 2]
     [round :south 1]
     [round :south 0]]]])
