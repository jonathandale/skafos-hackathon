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
  (fn-traced [{:keys [db]} [_ [team-1 team-2]]]
    {:db (-> db
           (dissoc :matchup :get-matchup-error)
           (assoc :fetching? true))
     :http-xhrio (merge base-request
                   {:headers         {:X-API-TOKEN config/x-api-token}
                    :method          :get
                    :params          {:where
                                      (app.utils/clj->json
                                         {"team1" {"type" "text"
                                                   "value" (:id team-1)}
                                          "team2" {"type" "text"
                                                   "value" (:id team-2)}})}
                    :uri             (str config/api-url
                                      "/data/cb571a77b504cc24ebc883d0/matchups")

                    :on-success      [::get-matchup-success]
                    :on-failure      [::get-matchup-error]})}))

(re-frame/reg-event-fx
 ::get-matchup-success
 [->kebab-case]
 (fn-traced [{:keys [db]} [_ results]]
   (let [data (first (:data results))
         team-1 (:team-1 data)
         team-2 (:team-2 data)]
      {:db (-> db
            (assoc-in [:matchups [team-1 team-2]] (:prob data))
            (dissoc :fetching?))})))

(re-frame/reg-event-fx
 ::get-matchup-error
  (fn [db [_ error]]
   (-> db
     (assoc :matchup-error error)
     (dissoc :fetching?))))


;; Bracket events
(defn- calc-next-position [round-index index group-index next-round]
  (.floor js/Math (/ (* (- 2 round-index) group-index)
                     (count next-round))))

(re-frame/reg-event-fx
  ::select-team
  (fn-traced
    [{db :db}
     [_ team {:keys [round-index group-index index teams region]}]]
    (let [next-round (get-in db [:bracket region (inc round-index)])
          next-group (calc-next-position round-index index group-index next-round)
          updated-db (-> db
                       (update-in [:bracket region round-index group-index index]
                         (fn [team]
                           (assoc team :selected true)))
                       (update-in [:bracket region (inc round-index) next-group]
                         (fn [new-group]
                           (conj new-group
                             (assoc team :index (if (even? group-index)
                                                  0 1))))))]
      (merge
        {:db updated-db}
        (when (= 1 (count (nth next-round next-group)))
          {:dispatch [::get-matchup teams]})))))
