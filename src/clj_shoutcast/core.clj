(ns clj-shoutcast.core
  (:import
   javazoom.jlgui.basicplayer.BasicPlayer
   java.net.URL
   java.net.Socket
   java.io.BufferedInputStream
   javazoom.spi.mpeg.sampled.file.IcyListener
   javazoom.spi.mpeg.sampled.file.tag.IcyInputStream
   net.sourceforge.jaad.spi.javasound.AACAudioFileReader
   net.sourceforge.jaad.spi.javasound.MP4AudioFileReader
   org.kc7bfi.jflac.sound.spi.FlacAudioFileReader
   javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader
   javazoom.spi.mpeg.sampled.file.MpegAudioFileReader
   javax.sound.sampled.AudioSystem
   javazoom.spi.mpeg.sampled.file.tag.TagParseListener
   ))

(def url (URL. "http://server1.chilltrax.com:9000/"))
(def icy-header (slurp "header.txt"))

(comment m_audioInputStream = typedAudioStream.audioInputStream
    m_audioFileFormat = typedAudioStream.audioFileFormat match {
      case Some(f) => f
      case None => throw new UnsupportedAudioFileException("stream is not a supported file type")
    }
    guessedType = typedAudioStream.guessedAudioCodec
    )

(def formats #{:RAW_AAC :MP4_AAC :NATIVE_FLAC :OGG_VORBIS :NATIVE_MP3})

(defn mime->format [mime]
  (cond
   (#{"audio/aac" "audio/aacp"} mime) :RAW_AAC
   (#{"audio/mp4" "application/mp4" "audio/x-mp4" "audio/x-m4a" "audio/mpeg4-generic" "audio/mp4-generic" "audio/MP4A-LATM"} mime) :MP4_AAC
   (#{"audio/flac" "audio/x-flac"} mime) :NATIVE_FLAC
   (#{"audio/ogg" "audio/vorbis" "audio/x-ogg" "application/ogg" "application/x-ogg" "audio/vorbis-config"} mime) :OGG_VORBIS
   (#{"audio/mpeg" "audio/mpeg3" "audio/x-mpeg3" "audio/mp3" "audio/x-mpeg" "audio/x-mp3" "audio/mpg" "audio/x-mpg" "audio/xmpegaudio"} mime) :NATIVE_MP3
   ))

(defn check-file-ending [url]
  (let [
        suffix (-> url .getFile (.split "\\.") last)
        ]
    (condp = suffix
      "aac" :RAW_AAC
      "m4a" :MP4_AAC
      "mp4" :MP4_AAC
      "flac" :NATIVE_FLAC
      "ogg" :OGG_VORBIS
      "oga" :OGG_VORBIS
      "mp3" :NATIVE_MP3)))

(defn read-and-reset [in j f]
  (let [
        in (if (.markSupported in) in (BufferedInputStream. in))
        ]
    (try
      (.mark in j)
      (f in)
      (finally (.reset in)))))



(defn audio-file-format [in file-type]
  (condp = file-type
    :RAW_AAC (.getAudioFileFormat (AACAudioFileReader.) in)
    :MP4_AAC (.getAudioFileFormat (MP4AudioFileReader.) in)
    :NATIVE_FLAC (.getAudioFileFormat (FlacAudioFileReader.) in)
    :OGG_VORBIS (.getAudioFileFormat (VorbisAudioFileReader.) in)
    :NATIVE_MP3 (.getAudioFileFormat (MpegAudioFileReader.) in)
    (AudioSystem/getAudioFileFormat in)
  ))


(defn audio-input-stream [in file-type]
  (condp = file-type
    :RAW_AAC (.getAudioInputStream (AACAudioFileReader.) in)
    :MP4_AAC (.getAudioInputStream (MP4AudioFileReader.) in)
    :NATIVE_FLAC (.getAudioInputStream (FlacAudioFileReader.) in)
    :OGG_VORBIS (.getAudioInputStream (VorbisAudioFileReader.) in)
    :NATIVE_MP3 (.getAudioInputStream (MpegAudioFileReader.) in)
    (AudioSystem/getAudioInputStream in)
  ))

(defn workaround [url]
  (let [
       socket (Socket. (.getHost url) (.getPort url))
       os (.getOutputStream socket)
       path (.getPath url)
       _ (.write os (.getBytes icy-header))
       buffered-input-stream (BufferedInputStream. (.getInputStream socket))
       stream (IcyInputStream. buffered-input-stream)
       mime (-> stream (.getTag "content-type") .getValue)
       stream-format (or (mime->format mime) (check-file-ending url))
       forced-format (read-and-reset buffered-input-stream 1000
                                     (fn [in] (audio-file-format in stream-format)))
       ]
    {:codec stream-format :in (audio-input-stream stream stream-format) :format forced-format
     :iis stream}
    ))

(defn print-tag-parse-event [tag-parse-event]
  (let [
        t (.getTag tag-parse-event)
        ]
    (println (str (.getName t) ": " (.getValue t)))))

(defn get-player [gain-f]
  (proxy [BasicPlayer TagParseListener] []
    (initAudioInputStream
     [url]
      (let [{:keys [in format iis]} (workaround url)]
        (.addTagParseListener iis this)
        (proxy-super setDouble in format)))
    (tagParsed [tag-parse-event]
      (proxy-super setGain (gain-f))
      (print-tag-parse-event tag-parse-event))
     ))

(defn boost-bass [player]
  (let [
        eq (-> player .getAudioInputStream .properties (get "mp3.equalizer"))
        ]
    (doseq [i (range 32)]
      (aset eq i (float (- 1 (/ (* i 2) 32.)))))))
