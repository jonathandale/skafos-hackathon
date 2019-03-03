(ns app.containers.bracket
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [app.subs :as subs]
            [app.events :as events]
            [app.utils :refer [log]]))

(def base-team-classes
  ["rounded-sm" "text-sm" "px-2" "py-2" "mx-1"])

(def active-team-classes
  (into base-team-classes
    ["cursor-pointer" "text-white" "hover:bg-grey-darkest"]))

(def inactive-team-classes
  (into base-team-classes
    ["bg-grey-lighter"]))

(def selected-team-classes
  (into base-team-classes
    ["bg-grey"]))

(def pending-team-classes
  (into base-team-classes
    ["bg-grey-light" "text-grey-light" "rounded-b-none" "border-b" "border-white"]))

(defn- team-item [team matchup-info probs]
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
        [:p.truncate
         [:span.text-xs.opacity-75 (:ranking team)]
         [:span.text-xs " " (:name team)]]]
       (when-not (and (some? @probs) disabled?)
         (let [win-% (* 100 (get @probs (keyword (str "team-" (inc (:index team)) "-win"))))]
           [:div.absolute.pin-y.pin-l.rounded-sm.rounded-r-none
            ; {:class [(str "bg-" (name (:color matchup-info)) "-darkest")]}
            {:class ["bg-black"]
             :style {:width (str win-% "%")
                     :opacity "0.85"}}]))])))

(defn- empty-team []
  [:li {:class pending-team-classes}
   [:p [:span.text-xs.opacity-75 "."]
       [:span.text-xs "."]]])

(defn- matchup
  [{:keys [teams round-index group-index group-height]
    :as matchup-info}]
  (let [get-team (fn [teams index]
                   ( first (filter #(= index (:index %)) teams)))
        probs (subscribe [::subs/matchup teams])]
    (fn []
      [:div.flex.flex-col.justify-center
       {:style {:height (str group-height "%")}}
       [:ul.list-reset.py-3
        (if-let [t0 (get-team teams 0)]
          [team-item t0 (assoc matchup-info :index (:index t0)) probs]
          [empty-team])
        (if-let [t1 (get-team teams 1)]
          [team-item t1 (assoc matchup-info :index (:index t1)) probs]
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

(defn- championship []
  (let [team1 (subscribe [::subs/finalist :team-1])
        team2 (subscribe [::subs/finalist :team-2])]
    (fn []
      [:div.w-1:3.px-2.mb-2
       [:div
        [:p.text-3xl.font-light.text-grey-dark.mb-3.text-center
         "Championship"]
        [:div.flex.text-center
         [:p.bg-indigo.w-1:2.mx-1.p-4.rounded-sm.truncate.text-white
          [:span.mr-1 (if @team1
                        (:name @team1)
                        "------")]
          (when @team1 [:span.opacity-50 "(" (:ranking @team1) ")"])]
         [:p.bg-indigo.w-1:2.mx-1.p-4.rounded-sm.truncate.text-white
          [:span.mr-1 (if @team2
                        (:name @team2)
                        "------")]
          (when @team2 [:span.opacity-50 "(" (:ranking @team2) ")"])]]]])))

(defn- final-four-team [team position]
  (let [selected (subscribe [::subs/finalist position])]
    (fn []
      (log @selected)
      [:p.bg-white.w-1:2.mx-1.p-4.rounded-sm.truncate
       {:on-click #(when-not @selected
                     (dispatch [::events/set-finalist position @team]))
        :class (when-not @selected
                 ["hover:bg-grey-light"
                  "cursor-pointer"])}
       [:span.mr-1 (if @team
                     (:name @team)
                     "------")]
       (when @team [:span.opacity-50 "(" (:ranking @team) ")"])])))

(defn- final-four-matchup [title team1 team2 position]
  (let [])
  (fn []
    [:div.w-1:3.px-2.mb-2
     [:div
      [:p.text-3xl.font-light.text-grey-dark.mb-3.text-center
       title]
      [:div.flex.text-center
       [final-four-team team1 position]
       [final-four-team team2 position]]]]))

(defn final-four []
  (let [east (subscribe [::subs/bracket-contender :east])
        west (subscribe [::subs/bracket-contender :west])
        midwest (subscribe [::subs/bracket-contender :midwest])
        south (subscribe [::subs/bracket-contender :south])]
    (fn []
      [:div.p-8.bg-grey-darkest.flex
       [final-four-matchup "East vs Midwest" east midwest :team-1]
       [championship]
       [final-four-matchup "West vs South" west south :team-2]])))

(defn render []
  [:div
   [:div.px-8.pt-8.text-center
    [:p.text-3xl.font-light.text-grey-darker
     "NCAA 2019 Men's basketball " [:span.text-grey " Un"] "Official Bracket"]]
   [:div.flex.m-8.bg-white.rounded.shadow
    [:div.w-1:2.p-6
     [:p.font-light.text-2xl.p-1.text-grey-dark "East"]
     [:div.flex.w-full
      [round :east :grey 0]
      [round :east :grey 1]
      [round :east :grey 2]
      [round :east :grey 3]]]
    [:div.w-1:2.p-6
     [:p.font-light.text-2xl.p-1.text-right.text-grey-dark "West"]
     [:div.flex.w-full
      [round :west :grey 3]
      [round :west :grey 2]
      [round :west :grey 1]
      [round :west :grey 0]]]]
   [final-four]
   [:div.flex.m-8.bg-white.rounded.shadow
    [:div.w-1:2.p-6
     [:p.font-light.text-2xl.p-1.text-grey-dark "Midwest"]
     [:div.flex.w-full
      [round :midwest :grey 0]
      [round :midwest :grey 1]
      [round :midwest :grey 2]
      [round :midwest :grey 3]]]
    [:div.w-1:2.p-6
     [:p.font-light.text-2xl.p-1.text-right.text-grey-dark "South"]
     [:div.flex.w-full
      [round :south :grey 3]
      [round :south :grey 2]
      [round :south :grey 1]
      [round :south :grey 0]]]]])
