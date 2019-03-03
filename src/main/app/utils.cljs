(ns app.utils
  (:require [clojure.string :as str]))

(defn window-dimensions []
  [(.-innerWidth js/window)
   (.-innerHeight js/window)])

(defn raf-debounce [callback]
  (let [ticking (atom false)]
    (fn []
      (when-not @ticking
        (.requestAnimationFrame js/window
          #(do
            (callback)
            (reset! ticking false)))
        (reset! ticking true)))))

(defn log [& args]
  (apply js/console.log args))

(defn clj->json [edn]
  (.stringify js/JSON (clj->js edn)))
