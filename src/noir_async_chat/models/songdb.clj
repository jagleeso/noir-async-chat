; parse an mp3 file and return tags (:artist :title)
(ns noir-async-chat.models.songdb
    (:require [clojure.java.shell :as shell]
              [claudio.id3]
              [clojure.java.io :as io]
              [clojure.test :refer [deftest is testing]]))

(defn tags [file]
    (claudio.id3/read-tag (clojure.java.io/file file)))

; (tags "/home/james/Music/Wolfmother/Wolfmother/Woman.mp3")

(defn sub-map 
  "Return a map with key-value pairs from map where key is in keys."
  [a-map the-keys]
  (reduce 
    (fn [m key] (conj m [key (key a-map)])) 
    {}
    the-keys))

(deftest sub-map-tests
  (testing "trivial"
    (are [expect got] (= expect got)
         {} (sub-map {} []) 
         {} (sub-map {} [:one]) 
         {} (sub-map {:one 1} [])))
  (are [expect got] (= expect got)
       {} (sub-map {} []) 
       {:one 1} (sub-map {:one 1 :two 2})))

; (sub-map (tags "/home/james/Music/Wolfmother/Wolfmother/Woman.mp3") [:artist, :album])

(defn all-tags 
  "Returns a lazy seq of :artist, :album, and :title tags for all mp3 files found by 
  recursively searching through directory."
  [directory]
  (map 
    (fn [mp3-file]
      (sub-map (tags mp3-file) [:artist, :album, :title]))
    (filter 
      #(.matches (.getName %) ".*.mp3$") 
      (file-seq (clojure.java.io/file directory)))))

; (clojure.pprint/pprint (all-tags (clojure.java.io/file "/home/james/Music/AFI/Decemberunderground")))
; (clojure.pprint/pprint (all-tags "/home/james/Music/AFI/Decemberunderground"))
