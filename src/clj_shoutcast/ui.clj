(ns clj-shoutcast.ui
 (:require [clj-shoutcast.core :as core]
           [clj-shoutcast.recorder :as recorder])
 (:import java.net.URL))

(def url (URL. "http://server1.chilltrax.com:9000/"))

(defn -main [& args]
  (let [
        player (core/get-player)
        ]
    (doto player (.open url) .play (.setGain 0.2) core/boost-bass)
    (println "playing...")
    (while true
      (print "=>")
      (.flush *out*)
      (let [
            input (.trim (read-line))
            val (try (Double/parseDouble input) (catch NumberFormatException e))
            ]
        (if val
          (.setGain player (/ val 9))
          (condp = input
            "r" (.resume player)
            "p" (.pause player)
            "m" (.toggleMute player)
            "save" (do (println "saving...") (reset! recorder/save? true))
            "cancel" (do (println "cancelling save...") (reset! recorder/save? false))
            "size" (-> recorder/os .size println)
            (println "command not recognized")
            ))))))
