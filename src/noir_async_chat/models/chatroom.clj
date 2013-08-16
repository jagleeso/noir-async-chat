(ns noir-async-chat.models.chatroom
  (:require
    [cheshire.core :as json]
    [noir-async-chat.models.songdb :as songdb])
  (:use lamina.core
        noir-async.utils))

; ----------------------------------------------------------------------------------------
; chatroom

(defprotocol IChatroom
  ""
  (has-user? [this handle])
  (add-user [this handle])
  (remove-user [this handle])
  (current-users [this])
  (user-count [this])
  (broadcast-handles [this])
  (send-chat [this handle message])
  (send-songdb [this handle])
  (send-priv [this handle mtype data])
  (subscribe-channel [this ch])
  (subscribe-priv-channel [this handle ch])
  (print-all [this]))

(deftype chatroom 
  ; "chat-channel broadcasts to other users in this chatroom."
  [name handles chat-channel priv-channels music-dir]
  IChatroom

  (has-user? [this handle]
    (dosync
      (get (ensure handles) handle)))

  (add-user [this handle]
    (dosync
        (if (has-user? this handle)
          false
          (alter handles conj handle))))
   
  (remove-user [this handle]
    (dosync
      (alter handles disj handle)))

  (current-users [this]
    @handles)

  (user-count [this]
    (count @handles))

  (broadcast-handles [this]
    (enqueue chat-channel
      (json/generate-string
        {:mtype "all-handles"
         :data (vec @handles)})))

  (send-chat [this handle message]
    (enqueue chat-channel
      (json/generate-string
        {:mtype "chat"
         :data  {:handle handle :text message}})))

  (send-songdb [this handle]
    (send-priv this handle "songdb" (songdb/all-tags music-dir)))

  (send-priv [this handle mtype data]
    (enqueue (@priv-channels handle)
             (json/generate-string
               {:mtype mtype
                :data data})))

  (subscribe-channel [this ch]
    (siphon chat-channel ch))

  (subscribe-priv-channel [this handle ch]
    (let [priv-channel (permanent-channel)]
      (siphon priv-channel ch)
      (dosync
        (alter priv-channels assoc handle priv-channel))))

  (print-all [this] 
    (receive-all chat-channel #(println (str "CH> " %1)))))

(defn make-chatroom [name music-dir]
  ; name
  ; handles
  ; chat-channel
  ; priv-channels
  ; music-dir
  (->chatroom name (ref #{}) (permanent-channel) (ref {}) music-dir))

; ----------------------------------------------------------------------------------------

; (receive-all chat-channel #(println (str "CH> " %1)))
