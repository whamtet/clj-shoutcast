(ns clj-shoutcast.recorder
  (:import javazoom.jlgui.basicplayer.BasicPlayerListener
           javax.sound.sampled.AudioSystem
           javax.sound.sampled.AudioInputStream
           javazoom.spi.mpeg.sampled.file.tag.TagParseListener
           )
  )

(def wave-audio-file-type (some #(if (= "WAVE" (str %)) %) (AudioSystem/getAudioFileTypes)))

(def save-dir (doto (java.io.File. "songs") .mkdir))

(defn save-file [player name os]
  (let [
        audio-format (-> player .getAudioInputStream .getFormat)
        bytes (.toByteArray os)
        stream (java.io.ByteArrayInputStream. bytes)
        audio-input-stream (AudioInputStream. stream audio-format (alength bytes))
        out-file (java.io.File. save-dir (str (.trim name) ".wav"))
        ]
    (AudioSystem/write audio-input-stream wave-audio-file-type out-file)
    )
  )

(def os (java.io.ByteArrayOutputStream.))
(def curr-name (atom nil))
(def save? (atom false))

(def recorder
    (reify
      BasicPlayerListener
      (opened [this _ properties]
       )
      (progress [this bytes-read microseconds pcm props]
        (.write os pcm)
                )
      (stateUpdated [this _])
      (setController [this _])
      ))

(defn tag-parse-listener [player]
  (.reset os)
  (reify
    TagParseListener
    (tagParsed [this tag-parse-event]
      (let [
            t (.getTag tag-parse-event)
            ]
        (when (= (.getName t) "StreamTitle")
          (when (and @save? @curr-name)
            (reset! save? false)
            (save-file player @curr-name os)
            (.reset os)
            )
          (reset! curr-name (.getValue t))
          )))))
