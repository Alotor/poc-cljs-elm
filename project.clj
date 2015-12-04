(defproject test-arq "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [prismatic/schema "1.0.3"]
                 [rum "0.6.0"]
                 [funcool/beicon "0.3.0-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]

                :figwheel {:on-jsload "test-arq.core/on-js-reload"}

                :compiler {:main test-arq.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/test_arq.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/test_arq.js"
                           :main test-arq.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:server-port 3333
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             })
