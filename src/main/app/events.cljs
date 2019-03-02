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


;; Bracket events

(defn- calc-next-position [team-index group-index next-round]
  (.floor js/Math (/ (* 2 group-index) (count next-round))))

(re-frame/reg-event-db
  ::select-team
  (fn-traced [db [_ {:keys [round-index
                            group-index
                            team-index
                            teams]}]]
    (let [next-round (get-in db [:bracket (inc round-index)])
          next-group (calc-next-position team-index group-index next-round)
          team (nth teams team-index)]
      (log "Update " team " into" next-group)
      (-> db
        (update-in [:bracket round-index group-index team-index]
          (fn [team]
            (assoc team :selected true)))
        (update-in [:bracket (inc round-index) next-group]
          (fn [new-group]
            (conj new-group team)))))))
