(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))

(def base-team-classes
  ["rounded-sm" "text-sm" "px-2" "py-1" "mx-1"])

(def active-team-classes
  (into base-team-classes
    ["cursor-pointer" "text-white"]))

(def inactive-team-classes
  (into base-team-classes
    ["bg-grey-lighter"]))

(def selected-team-classes
  (into base-team-classes
    ["bg-grey"]))

(def pending-team-classes
  (into base-team-classes
    ["bg-grey-lighter" "text-grey-lighter" "rounded-b-none" "border-b" "border-white"]))

(defn- team-item [team matchup-info]
  (fn []
    (let [disabled? (some :selected (:teams matchup-info))]
      [:li.relative
       {:on-click #(when-not disabled?
                     (dispatch [::events/select-team team matchup-info]))
        :class (concat
                 (cond
                   (true? (:selected team))
                   selected-team-classes

                   (some? disabled?)
                   inactive-team-classes

                   :else
                   active-team-classes)
                 (if (= 1 (:seed team))
                   ["rounded-b-none" "border-b" "border-white"]
                   ["rounded-t-none"])
                 [(str "bg-" (name (:color matchup-info)) "-darker")])}
       [:div.relative.z-10
        [:p [:span.text-xs.opacity-75 (:ranking team)]
            [:span.text-xs " " (:name team)]]]
       (when-not disabled?
         [:div.absolute.pin-y.pin-l.rounded-sm.rounded-r-none
          {:class [(str "bg-" (name (:color matchup-info)) "-darkest")]
           :style {:width "29%"
                   :opacity "0.8"}}])])))

(defn- empty-team []
  [:li {:class pending-team-classes}
   [:p [:span.text-xs.opacity-75 "."]
       [:span.text-xs "."]]])

(defn- matchup
  [{:keys [teams round-index group-index group-height]
    :as matchup-info}]
  (let [get-team (fn [teams index]
                   (first (filter #(= index (:index %)) teams)))]
    (fn []
      (log "matchup" teams)
      [:div.flex.flex-col.justify-center
       {:style {:height (str group-height "%")}}
       [:ul.list-reset.py-3
        (if-let [t0 (get-team teams 0)]
          [team-item t0 (assoc matchup-info :index (:index t0))]
          [empty-team])
        (if-let [t1 (get-team teams 1)]
          [team-item t1 (assoc matchup-info :index (:index t1))]
          [empty-team])]])))

(defn round [region color round-index]
  (let [round-teams (subscribe [::subs/bracket region round-index])]
    (fn []
      (let [group-height (/ 100 (count @round-teams))]
        [:ul.list-reset.w-1:4
         (map-indexed
           (fn [group-index teams]
             ^{:key (str group-index round-index teams)}
              [matchup {:teams teams
                        :region region
                        :color color
                        :round-index round-index
                        :group-index group-index
                        :group-height group-height}])
           @round-teams)]))))

(defn render []
  [:div
   [:div.flex
    [:div.flex.w-1:2.p-4
     [round :east :grey 0]
     [round :east :grey 1]
     [round :east :grey 2]
     [round :east :grey 3]]
    [:div.flex.w-1:2.p-4
     [round :west :grey 3]
     [round :west :grey 2]
     [round :west :grey 1]
     [round :west :grey 0]]]
   [:div.p-8.bg-grey-lighter]
   [:div.flex
    [:div.flex.w-1:2.p-4
     [round :midwest :grey 0]
     [round :midwest :grey 1]
     [round :midwest :grey 2]
     [round :midwest :grey 3]]
    [:div.flex.w-1:2.p-4
     [round :south :grey 3]
     [round :south :grey 2]
     [round :south :grey 1]
     [round :south :grey 0]]]])
