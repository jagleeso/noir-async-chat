(ns noir-async-chat.models.songdb-spec
  (:require [noir-async-chat.models.songdb :refer [sub-map]])
  (:use [speclj.core]))

; Run all speclj tests repeatedly, watching for file changes by doing:
;
; lein spec -a
;
; Looks like speclj is smart enough to do the following:
; 1. run any spec/*_spec.clj everytime it is written
; 2. rerun spec/%_spec.clj if there is a matching src/%.clj that gets written
;
; Would be nice if instead of 1., it did:
; a. rerun any spec files the require (directly or indirectly) a written file (like a 
; dependency graph)
;
; I guess 1. catches all a. cases, so whatever.

(describe "test that sub-map,"
  (describe "on trivial cases,"
    (it "handles empty keys and map"
      (should= {} (sub-map {} [])))
    (it "handles just empty keys"
      (should= {} (sub-map {:one 1} [])))
    (it "handles just empty map"
      (should= {:one nil} (sub-map {} [:one])))))

(run-specs)
