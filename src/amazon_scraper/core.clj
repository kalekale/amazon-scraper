(ns amazon-scraper.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(use '[clj-webdriver.taxi :as taxi])

(use '[clj-webdriver.driver :only [init-driver]])

(def ^:dynamic *base-url* "https://www.amazon.com/gp/goldbox")
(def ^:dynamic *base-url-2* "https://www.amazon.com/gp/goldbox/ref=gbps_ftr_s-3_9822_page_2?gb_f_GB-SUPPLE=page:2")
(def ^:dynamic *base-url-3* "https://www.amazon.com/gp/goldbox/ref=gbps_ftr_s-3_9822_page_2?gb_f_GB-SUPPLE=page:3")
(def ^:dynamic *base-url-4* "https://www.amazon.com/gp/goldbox/ref=gbps_ftr_s-3_9822_page_2?gb_f_GB-SUPPLE=page:4")
(def ^:dynamic *base-url-5* "https://www.amazon.com/gp/goldbox/ref=gbps_ftr_s-3_9822_page_2?gb_f_GB-SUPPLE=page:5")

(def first-pages
  [*base-url* *base-url-2* *base-url-3* *base-url-4* *base-url-5*])

(defn driver []
  (taxi/new-driver {:browser :chrome}))

(defn set-up-driver []
  (set-driver! (driver)))

(defn get-links [d url]
  (taxi/to d url)
  (taxi/execute-script d "window.scrollTo(0, document.body.scrollHeight)")
  (doall (map #(taxi/attribute d %1 :href) (taxi/find-elements d {:id "dealImage"})))) 

(defn test-driver []
  (let [d (set-up-driver)]
    (get-links d *base-url*)))

(defn truncate
  [s n] 
  (apply str (take n s)))

(defn screenshot-product-new-driver [url]
  (set-up-driver)
  (taxi/to url)
  (taxi/take-screenshot :file (truncate (str "./" (str/replace url #"/" "*") ".png") 100)))

(defn screenshot-product [d url]
  (taxi/to d url)
  (taxi/take-screenshot d :file (truncate (str "./" (str/replace url #"/" "*") ".png") 100)))

(defn screenshot-all-products-on-page [d url]
  (map #(screenshot-product d %1) (get-links d url)))

(defn all-pages [urls]
  (map #(screenshot-all-products-on-page (set-up-driver) %1) urls))

(defn create-agents [url]
  (set-up-driver)
  (taxi/to url))

(defn run-async-all-products []
  (let [agents (for [url (get-links)] (agent url))]
    (doseq [agent agents]
      (send-off agent screenshot-product))))


