(ns noir-async-chat.models.chatroom
  (:require
    [cheshire.core :as json])
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
  (subscribe-channel [this ch])
  (print-all [this]))

(deftype chatroom [name handles chat-channel]
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

  (subscribe-channel [this ch]
    (siphon chat-channel ch))

  (print-all [this] 
    (receive-all chat-channel #(println (str "CH> " %1)))))

(defn make-chatroom [name]
  (->chatroom name (ref #{}) (permanent-channel)))

; ----------------------------------------------------------------------------------------

; (receive-all chat-channel #(println (str "CH> " %1)))
