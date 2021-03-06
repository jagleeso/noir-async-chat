(defproject noir-async-chat "1.0.0-SNAPSHOT"
  :main noir-async-chat.server
  :description "An example using noir-async"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [noir "1.3.0-beta3"]
                 [noir-async "1.1.0-beta11"]
                 [claudio "0.1.2"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec"])
