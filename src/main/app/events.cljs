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
  (fn-traced [{:keys [db]} [_ teams]]
    (let [team1 (first teams)
          team2 (last teams)]
      (prn "team1" team1)
      (prn "team2" team2)
      {:db (-> db
             (dissoc :matchup :get-matchup-error)
             (assoc :fetching? true))
             ; {"team1" : {"type" : "text", "value" : "1231"}, "team2" : {"type": "text", "value" : "1532"}}
       :http-xhrio (merge base-request
                     {:headers         {"authorization" "Bearer 9cfd5be7be07452c8bc689bd1967a1a79767dc299c9c2ff57d20406d599f3803"}
                      :method          :get
                      :params          {:where
                                        (app.utils/clj->json
                                           {"team1" {"type" "text"
                                                     "value" (:id team1)}
                                            "team2" {"type" "text"
                                                     "value" (:id team2)}})}
                      :uri             (str config/api-url
                                        "/data/cb571a77b504cc24ebc883d0/matchups")

                      :on-success      [:get-matchup-success]
                      :on-failure      [:get-matchup-error]})})))

(re-frame/reg-event-fx
 ::get-matchup-success
 [->kebab-case]
 (fn-traced [{:keys [db]} [_ results]]
   (prn "matchup results" results)
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
  (.floor js/Math (/ (* (- 2 round-index) group-index)
                     (count next-round))))

(re-frame/reg-event-fx
  ::select-team
  (fn-traced [{db :db} [_ {:keys [round-index
                                  group-index
                                  team-index
                                  teams]}]]
    (let [next-round (get-in db [:bracket (inc round-index)])
          next-group (calc-next-position round-index team-index group-index next-round)
          team (nth teams team-index)
          updated-db (-> db
                       (update-in [:bracket round-index group-index team-index]
                         (fn [team]
                           (assoc team :selected true)))
                       (update-in [:bracket (inc round-index) next-group]
                         (fn [new-group]
                           (conj new-group team))))]
        (prn "next round" next-round)
        (prn "next group" next-group)
        (merge
          {:db updated-db}
          (when (= 1 (count (nth next-round next-group)))
            {:dispatch [::get-matchup teams]})))))
