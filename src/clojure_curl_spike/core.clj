(ns clojure-curl-spike.core
  (:use [clojure.java.shell :only (sh)]
        [clojure.string :only (join)]))

(def stats-list [:time_total
                 :time_namelookup
                 :time_connect
                 :time_starttransfer
                 :http_code])

(defn build-stats-pair [stat]
  (str (keyword stat) " " "%{" (name stat) "}"))

(defn build-stats [list]
  (let [body (join " " (map build-stats-pair list))]
    (str "{" body "}")))

(def curl-stats
  (build-stats stats-list))

(defn curl-command [url]
  ["curl" url "-o" "/dev/null" "-s" "-w" curl-stats])

(defn curl [url]
  (apply sh (curl-command url)))

(defn get-output [status]
  (let [exit (:exit status)
        out  (load-string (:out status))]
    (merge {:exit exit} out)))

(defn fetch-url [url]
  (merge {:url url} (get-output (curl url))))

(defn fetch-multi [urls]
  (pmap fetch-url urls))