(defproject amazon-scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Dphantomjs.binary.path=./phantomjs-2.1.1-macosx/bin/phantomjs"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [enlive "1.1.1"]
                 [ring "1.2.0"]
                 [net.cgrand/moustache "1.1.0"]
                 [clj-webdriver/clj-webdriver "0.6.0"]
                 [com.github.detro.ghostdriver/phantomjsdriver "1.0.3"]]
  :main ^:skip-aot amazon-scraper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
