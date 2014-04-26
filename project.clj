(defproject clj-shoutcast "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;poopoo
                 [self/basicplayer "3.0"]
                 [self/commonsloggingapi "1.1.1"]
                 [self/jaadmod "0.8.4"]
                 [self/jflac "1.3"]
                 [self/jl "1.0.1"]
                 [self/jorbis "0.0.17"]
                 [self/mp3spi "1.9.4"]
                 [self/tritonus_share "0.3.6.1"]
                 [self/vorbisspi "1.0.3"]

                 ]
  :repositories [["repo" {:url "http://eclipse-repo.appspot.com/repo"
                          :checksum :ignore}
                          ]]
  :jvm-opts ["-Xmx1g"]
  )
