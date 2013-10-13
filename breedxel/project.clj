(defproject breedxel "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [jayq "2.4.0"]
                 [org.clojure/core.async "0.1.222.0-83d0c2-alpha"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :cljsbuild {
              :builds {
                       :app
                       {:source-paths ["src"]
                        :compiler {:output-to "app/app.js"}}}})
