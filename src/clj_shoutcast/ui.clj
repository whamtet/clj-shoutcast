(ns clj-shoutcast.ui
 (:require [clj-shoutcast.core :as core]
           [clj-shoutcast.recorder :as recorder]
           [clojure.java.shell :as shell]
           )
 (:import java.net.URL))

(def url (URL. "http://server1.chilltrax.com:9000/"))
(def urls [url])

(def songs-dir (java.io.File. "songs"))

(defn get-songs []
  (filter #(.endsWith (.getName %) ".wav") (file-seq songs-dir)))

(defn open-all-cmd []
  (list* "open" (map #(.getPath %) (get-songs))))

(defn get-song [prefix]
  (some #(if (.startsWith (.toLowerCase (.getName %)) prefix) %) (get-songs)))

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
  (println (apply str (interpose "\n" (map #(.getName %) (get-songs)))))
  (println "***"))

(defn rm [command]
  (when-let [song (get-song (second (.split command " ")))]
    (println (format "Delete %s? " (.getName song)))
    (.flush *out*)
    (if (= "y" (.trim (read-line))) (.delete song))))

(let [
      command "rm A"
      [_ prefix] (.split command " ")
      song (get-song prefix)
      ]
  song)

(defn -main [& [url-index]]
  (let [
        url-index (if url-index
                    (try (Integer/parseInt url-index) (catch NumberFormatException e 0))
                    0)
        url (nth urls url-index)
        player (core/get-player)
        ]
    (doto player (.open url) .play (.setGain 0.2) (core/boost-bass true))
    (println "playing...")
    (while true
      (print "=>")
      (.flush *out*)
      (let [
            input (-> (read-line) .trim .toLowerCase)
            val (try (Double/parseDouble input) (catch NumberFormatException e))
            starts-with? (fn [a b] (.startsWith b a))
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
            (condp starts-with? input
              "rm" (rm input)
              "open" (open input player)
              (println "command not recognized"))
            ))))))
