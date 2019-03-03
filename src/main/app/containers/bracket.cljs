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

(defn- team-item [team matchup-info probs]
  (fn []
    (let [disabled? (some :selected (:teams matchup-info))]
      (log "win prob" @probs)
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
            [:span.text-xs " " (:name team)]
            [:span.text-xs " " probs]]]
       (when-not disabled?
         [:div.absolute.pin-y.pin-l.rounded-sm.rounded-r-none
          {:class [(str "bg-" (name (:color matchup-info)) "-darkest")]
           :style {:width (str (* 10 probs) "%")
                   :opacity "0.8"}}])])))

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
      ; (log "probs" @probs)
      ; (log "team 1 win " (:team-1-win @probs))
      ; (log "team 2 win " (:team-2-win @probs))
      (log "matchup" teams)
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

(defn- final-four-matchup [title team1 team2]
  (fn []
    [:div.w-1:3.px-2.mb-2
     [:div
      [:p.text-3xl.font-light.text-grey-dark.mb-3.text-center
       title]
      [:div.flex.text-center
       [:p.bg-white.w-1:2.mx-1.p-4.rounded-sm
        [:span.mr-1 (if @team1
                      (:name @team1)
                      "------")]
        [:span.opacity-75 (:ranking @team1)]]
       [:p.bg-white.w-1:2.mx-1.p-4.rounded-sm
        [:span.mr-1 (if @team2
                      (:name @team2)
                      "------")]
        [:span.opacity-75 (:ranking @team2)]]]]]))

(defn final-four []
  (let [east (subscribe [::subs/bracket-contender :east])
        west (subscribe [::subs/bracket-contender :west])
        midwest (subscribe [::subs/bracket-contender :midwest])
        south (subscribe [::subs/bracket-contender :south])]
    (fn []
      [:div.p-8.bg-grey-darkest.flex
       [final-four-matchup "East vs Midwest" east midwest]
       [final-four-matchup "Championship" (atom nil) (atom nil)]
       [final-four-matchup "West vs South" west south]])))

(defn render []
  [:div
   [:div.px-8.pt-8.bg-grey-lightest.text-center
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
