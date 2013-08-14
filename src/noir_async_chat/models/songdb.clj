; parse an mp3 file and return tags (:artist :title)
(ns noir-async-chat.models.songdb
    (:require [ clojure.java.shell :as shell ]
              [ claudio.id3 ]))

(defn tags [file]
    (claudio.id3/read-tag file))

; (tags "/home/james/Music/Wolfmother/Wolfmother/Woman.mp3")

(defn shell-tags [filepath]
    (shell/sh "id3v2" "--list" filepath))

(defn sub-map 
  "Return a map with key-value pairs from map where key is in keys."
  [map keys]
  (reduce 
    (fn [m key] (conj m [key (key map)])) 
    {}
    keys))

; (sub-map (tags "/home/james/Music/Wolfmother/Wolfmother/Woman.mp3") [:artist, :album])

(defn all-tags 
  "Returns a lazy seq of :artist, :album, and :title tags for all mp3 files found by 
  recursively searching through directory (a File)"
  [directory]
  (map 
    (fn [mp3-file]
      (sub-map (tags mp3-file) [:artist, :album, :title]))
    (filter 
      #(.matches (.getName %) ".*.mp3$") 
      (file-seq directory))))

; (clojure.pprint/pprint (all-tags (clojure.java.io/file "/home/james/Music/AFI/Decemberunderground")))
(class (all-tags (clojure.java.io/file "/home/james/Music/AFI/Decemberunderground")))
