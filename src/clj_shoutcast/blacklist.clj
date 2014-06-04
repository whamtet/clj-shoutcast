(ns clj-shoutcast.blacklist)

(def list-file (java.io.File. "blacklist.edn"))
(def blacklist (atom (if (.exists list-file) (read-string (slurp list-file)) #{})))

(defn blacklist! [f]
  (swap! blacklist #(conj % f))
  (spit list-file (pr-str @blacklist)))

(defn blacklisted? [f]
  (@blacklist f))
