(ns clj-shoutcast.ui
 (:require [clj-shoutcast.core :as core]
           [clj-shoutcast.recorder :as recorder]
           [clojure.java.shell :as shell]
           )
 (:import java.net.URL))

(def url (URL. "http://server1.chilltrax.com:9000/"))

(def songs-dir (java.io.File. "songs"))

(defn get-songs []
  (filter #(.endsWith (.getName %) ".wav") (file-seq songs-dir)))

(defn open-all-cmd []
  (list* "open" (map #(.getPath %) (get-songs))))

(defn get-song [prefix]
  (some #(if (.startsWith (.getName %) prefix) %) (get-songs)))

(defn open [command player]
  (let [
        [_ prefix] (.split command " ")
        ]
    (reset! core/hard-mute? true)
    (.setMute player true)
    (if prefix
      (if-let [song (get-song prefix)] (shell/sh "open" (.getPath song)))
      (apply shell/sh (open-all-cmd)))))

(defn ls []
  (println "***saved songs***")
  (println (apply str (interpose "\n" (map #(.getName %) (get-songs))))))


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
            input (-> (read-line) .trim .toLowerCase)
            val (try (Double/parseDouble input) (catch NumberFormatException e))
            ]
        (if val
          (.setGain player (/ val 9))
          (condp = input
            "r" (.resume player)
            "p" (.pause player)
            "m" (do (.toggleMute player) (reset! core/hard-mute? false))
            "save" (do (println "saving...") (reset! recorder/save? true))
            "cancel" (do (println "cancelling save...") (reset! recorder/save? false))
            "size" (-> recorder/os .size println)
            "ls" (ls)
            (if (.startsWith input "open")
              (open input player)
              (println "command not recognized"))
            ))))))
