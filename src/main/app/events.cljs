(ns app.events
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :refer [->interceptor]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [app.config :as config]
            [app.db :as db]
            [app.utils :refer [log]]))

(defn kebab-case [context]
  (update-in context [:coeffects :event] #(transform-keys ->kebab-case-keyword %)))

(def ->kebab-case (->interceptor :id :->kebab-case :before kebab-case))

(def base-request
  {:timeout         10000
   :response-format (ajax/json-response-format {:keywords? true})})

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_]
   db/default-db))

(re-frame/reg-event-db
 ::set-page
 (fn-traced [db [_ page q-params]]
   (assoc db :page page)))

(re-frame/reg-event-db
  ::set-window-dimensions
  (fn [db [_ dimensions]]
    (assoc db :window-dimensions dimensions)))

;; Round Probabilities
(re-frame/reg-event-fx
  ::get-matchup
  (fn-traced [{:keys [db]} [_]]
   {:db (-> db
          (dissoc :matchup :get-matchup-error)
          (assoc :fetching? true))
    :http-xhrio (merge base-request
                  {:headers         {"X-API-TOKEN" "a3c6df8ff9e507ccfbae15966d883747"}
                   :method          :get
                   :uri             (str config/api-url
                                         "/data/cb571a77b504cc24ebc883d0/matchup")
                   :on-success      [:get-matchup-success]
                   :on-failure      [:get-matchup-error]})}))

(re-frame/reg-event-fx
 ::get-matchup-success
 [->kebab-case]
 (fn-traced [{:keys [db]} [_ results]]
   (prn "matchup resutls" results)
   {:db (-> db
         (assoc :matchups results)
         (dissoc :fetching?))}))


(re-frame/reg-event-fx
 ::get-matchup-error
  (fn [db [_ error]]
   (-> db
     (assoc :matchup-error error)
     (dissoc :fetching?))))



;; Bracket events

(defn- calc-next-position [round-index team-index group-index next-round]
  (prn (str "team index: " team-index " group index: " group-index " next round: " (count next-round) "current round: " round-index))
  (prn (str "math before floor " (/ (* 2 group-index) (count next-round))))
  (.floor js/Math (/ (* (- 2 round-index) group-index) (count next-round))))

(re-frame/reg-event-db
  ::select-team
  (fn-traced [db [_ {:keys [round-index
                            group-index
                            team-index
                            teams]}]]
    (let [next-round (get-in db [:bracket (inc round-index)])
          next-group (calc-next-position round-index team-index group-index next-round)
          team (nth teams team-index)]
      (log "Update " team " into" next-group)
      (-> db
        (update-in [:bracket round-index group-index team-index]
          (fn [team]
            (assoc team :selected true)))
        (update-in [:bracket (inc round-index) next-group]
          (fn [new-group]
            (conj new-group team)))))))
