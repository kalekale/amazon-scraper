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

(def one-page [*base-url*])

(defn driver []
  (taxi/new-driver {:browser :chrome}))

(defn set-up-driver []
  (set-driver! (driver)))

(defn get-links [d url]
  (taxi/to d url)
  (taxi/execute-script d "window.scrollTo(0, document.body.scrollHeight)")
  (mapv #(taxi/attribute d % :href) (taxi/find-elements d {:id "dealImage"})))




(defn get-links-d [url]
  (let [d (set-up-driver)]
    (taxi/to d url)
    (taxi/execute-script d "window.scrollTo(0, document.body.scrollHeight)")
    (print "heihei")
    (let [x (doall (map #(taxi/attribute d %1 :href) (taxi/find-elements d {:id "dealImage"})))]
      x)))

(defn test-driver []
  (let [d (set-up-driver)]
    (get-links d *base-url*)))

(defn truncate
  [s n] 
  (apply str (take n s)))

(defn single-product? [d]
  (or
   (nil? (taxi/find-element d {:id "twister_feature_div"}))
   (clojure.string/blank? (taxi/attribute d (taxi/find-element d {:id "twister_feature_div"}) :text))
   (nil? (taxi/find-element d {:css ".a-nostyle.a-button-list.a-declarative.a-button-toggle-group.a-horizontal.a-spacing-top-micro.swatches"}))
   (nil? (taxi/find-element d {:css ".a-section.a-spacing-none.twisterShelf_swatchSection"}))))



(defn multiple-options? [d]
  (not (single-product? d)))

(defn screenshot-product-new-driver [url]
  (set-up-driver)
  (taxi/to url)
  (taxi/take-screenshot :file (truncate (str "./" (str/replace url #"/" "*") ".png") 100)))

(defn screenshot-product [d url]
  (taxi/to d url)
  (taxi/take-screenshot d :file (truncate (str "./" (str/replace url #"/" "*") ".png") 100)))

(defn get-original-price [d]
  (let [elem (taxi/find-element d {:class "a-text-strike"})]
    (taxi/attribute d elem :text)))

(defn get-product-info [d url]
  (taxi/to d url)
  (if (is-multiple? d)
    (print (str "\n" "multiple" "\n" url "\n"))
    (print (str "\n" "single" "\n" url "\n"))))

(defn screenshot-all-products-on-page [url]
  (let [d (set-up-driver)]
    (let [products (get-links d url)]
      (print "***Products***\n")
      (print products)
      (print "\n")
      (print (count products))
      (doseq [product products]
        (screenshot-product d product)))))

(defn get-info-all-products-on-page [url]
  (let [d (driver)]
    (let [products (get-links d url)]
      (print (str "got-products " products))
      (doseq [product products]
        (get-product-info d product)))
    (taxi/quit d)))

(defn all-pages [urls]
  (doseq [url urls]
    (screenshot-all-products-on-page url)))

(defn create-agents [url]
  (set-up-driver)
  (taxi/to url))

(defn run-async-all-products []
  (let [agents (for [url (get-links)] (agent url)) ]
    (doseq [agent agents]
      (send-off agent screenshot-product))))

(defn async-screenshot-products-on-pages [urls]
  (let [agents (for [url urls] (agent url))]
    (print agents)
    (doseq [agent agents]
      (send-off agent screenshot-all-products-on-page))
    (apply await agents)))


(defn async-print-products-on-pages [urls]
  (let [agents (for [url urls] (agent url))]
    (doseq [agent agents]
      (send-off agent get-info-all-products-on-page))
    (apply await agents)
    (map #(print (str agent "\n")))))

(defn get-type [d]
  )


(def test-url "https://www.amazon.com/dp/B013OINHEA/ref=gbps_img_s-6_8fa1_0c9ddb16?smid=ABIV5SAQBP0DT&pf_rd_p=37acc6eb-e04d-4164-9e7a-4d5a6b188fa1&pf_rd_s=slot-6&pf_rd_t=701&pf_rd_i=gb_main&pf_rd_m=ATVPDKIKX0DER&pf_rd_r=H7THJ4H0FN5EBZW9FYSF")

(defn test-css []
  (set-up-driver)
  (taxi/to test-url)
  (or
   (nil? (taxi/find-element  {:id "twister_feature_div"}))
   (clojure.string/blank? (taxi/attribute  (taxi/find-element  {:id "twister_feature_div"}) :text))
   (nil? (taxi/find-element  {:css ".a-nostyle.a-button-list.a-declarative.a-button-toggle-group.a-horizontal.a-spacing-top-micro.swatches"}))
   (nil? (taxi/find-element  {:css ".a-section.a-spacing-none.twisterShelf_swatchSection"}))))

(defn is-multiple? [d]
  (or
   (not (nil? (taxi/find-element d {:css ".a-nostyle.a-button-list.a-declarative.a-button-toggle-group.a-horizontal.a-spacing-top-micro.swatches"})))
   (not (nil? (taxi/find-element d {:css ".a-section.a-spacing-none.twisterShelf_swatchSection"})))))
