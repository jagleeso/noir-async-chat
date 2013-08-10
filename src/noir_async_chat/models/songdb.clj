; parse an mp3 file and return tags (:artist :title)
(ns noir-async-chat.models.songdb
    (:require [ clojure.java.shell :as shell ]))

(defn id3v2-tags [file]
    (shell/sh "id3v2" "--list" file))
