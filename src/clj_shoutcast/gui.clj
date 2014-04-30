(ns clj-shoutcast.gui
  (:import shoutcast.gui.Notify
           shoutcast.gui.MainFrame
           javazoom.spi.mpeg.sampled.file.tag.TagParseListener
           java.net.URL
           )
  (:require [clj-shoutcast.core :as core]
            [clj-shoutcast.recorder :as recorder]
            )
  )

(defn tag-parse-listener [ui-ref]
  (reify TagParseListener
    (tagParsed [this tag-parse-event]
               (let [
                     t (.getTag tag-parse-event)
                     ]
                 (when (= (.getName t) "StreamTitle")
                   (.setSong @ui-ref (.getValue t)))))))

(defn notify [player]
  (reify Notify
    (setTempMute [_ mute?]
      (reset! core/hard-mute? false)
      (.setMute player mute?))
    (setPlay [_ play?]
      (reset! core/hard-mute? true)
      (.setMute player (not play?)))
    (setSave [_ save?] (reset! recorder/save? save?))
    (setVolume [_ volume] (.setGain player volume))
    ))

(def url (URL. "http://server1.chilltrax.com:9000/"))

(defn -main [& args]
  (let [
        ui-ref (atom nil)
        player (core/get-player (tag-parse-listener ui-ref))
        init-volume 0.2
        ui (MainFrame. (notify player) init-volume)
        ]
    (reset! ui-ref ui)
    (.setVisible ui true)
    (doto player (.open url) .play (.setGain init-volume) core/boost-bass)
  ))
