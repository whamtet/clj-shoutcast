(ns clj-shoutcast.edit)

(def jars (filter #(.endsWith (.getName %) ".jar")
                   (file-seq (java.io.File. "lib"))))

(defn prefix [f]
  (apply str (drop-last 4 (.getName f))))

(defn version [f]
  (-> f prefix (.split "-") last))

(defn artifact-id [f]
  (apply str (-> f prefix (.split "-") butlast)))

(defn install-str [f]
  (format "mvn install:install-file -Dfile=%s -DgroupId=self -DartifactId=%s -Dpackaging=jar -Dversion=%s -DlocalRepositoryPath=/Users/matthewmolloy/python/eclipse-repo/repo"
          (.getPath f) (artifact-id f) (version f)))

;(doseq [jar jars] (.exec (Runtime/getRuntime) (install-str jar)))

(defn project-str [f]
  (format "[self/%s \"%s\"]" (artifact-id f) (version f)))

(defn maven-str [f]
  (format "<dependency><groupId>self</groupId><artifactId>%s</artifactId><version>%s</version></dependency>
          " (artifact-id f) (version f)))

;(println (apply str (interpose "\n" (map maven-str jars))))

