(ns amazon-scraper.core
  (:require [net.cgrand.enlive-html :as html]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(use '[clj-webdriver.taxi :as taxi])

(use '[clj-webdriver.driver :only [init-driver]])

(def ^:dynamic *base-url* "https://www.amazon.com/gp/goldbox")

(defn driver []
  (taxi/new-driver {:browser :chrome}))


(defn set-up-driver []
  (set-driver! (driver)))


(defn get-links []
  (set-up-driver)
  (taxi/to *base-url*)
  (doall (map #(taxi/attribute %1 :href) (taxi/find-elements {:id "dealImage"}))))
 
(defn screenshot-product [url]
  (taxi/to url)
  )

(defn run []
  (map #(screenshot-product %1) (get-links)))
