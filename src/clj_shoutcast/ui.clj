(ns clj-shoutcast.ui
 (:require [clj-shoutcast.core :as core]
           [clj-shoutcast.recorder :as recorder])
 (:import java.net.URL))

(def url (URL. "http://server1.chilltrax.com:9000/"))

(defn -main [& args]
  (let [
        curr-vol (atom 0.5)
        player (core/get-player #(deref curr-vol))
        ]
    (doto player (.open url) .play (.setGain @curr-vol) core/boost-bass)
    (println "playing...")
    (while true
      (print "=>")
      (let [
            input (.trim (read-line))
            val (try (Integer/parseInt input) (catch NumberFormatException e))
            ]
        (if val
          (do (.setGain player (/ val 9)) (reset! curr-vol (/ val 9)))
          (condp = input
            "r" (.resume player)
            "p" (.pause player)
            "s" (.setGain player 0)
            "save" (do (println "saving...") (reset! recorder/save? true))
            nil
            ))))))
