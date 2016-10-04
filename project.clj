(defproject xpertview "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[ch.qos.logback/logback-classic "1.1.7"]
                 [re-frame "0.8.0"]
                 [cljs-ajax "0.5.8"]
                 [secretary "1.2.3"]
                 [reagent-utils "0.2.0"]
                 ;[reagent "0.6.0-rc"]
                 [reagent "0.6.0-rc" :exclusions [org.clojure/tools.reader cljsjs/react]]
                 [org.clojure/clojurescript "1.9.225" :scope "provided"]
                 [org.clojure/clojure "1.8.0"]
                 [selmer "1.0.7"]
                 [markdown-clj "0.9.89"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [bouncer "1.0.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.3"]
                 [org.webjars/font-awesome "4.6.3"]
                 [org.webjars.bower/tether "1.3.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.5.1"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.2.1"]
                 [luminus/ring-ttl-session "0.3.1"]
                 [mount "0.1.10"]
                 [cprop "0.1.9"]
                 [org.clojure/tools.cli "0.3.5"]
                 [cljs-react-material-ui "0.2.19"]
                 [luminus-nrepl "0.1.4"]
                 [buddy "1.0.0"]
                 [com.draines/postal "2.0.0"]
                 [com.taoensso/timbre "4.1.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [alandipert/storage-atom "2.0.1"]
                 [twilio-api "1.0.1"]
                 [bk/ring-gzip "0.1.1"]
                 [clj-oauth "1.5.4"]
                 [com.taoensso/timbre "4.1.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.datomic/datomic-free "0.9.5394" :exclusions [org.slf4j/log4j-over-slf4j org.slf4j/slf4j-nop com.google.guava/guava]]
                 [luminus-http-kit "0.1.4"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main xpertview.core

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.4"]]
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-to "target/cljsbuild/public/js/app.js"
                 :externs ["react/externs/react.js"]
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}}}}}
             
             
             :aot :all
             :uberjar-name "xpertview.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.1.1"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.5.0"]
                                 [pjstadig/humane-test-output "0.8.1"]
                                 [doo "0.1.7"]
                                 [binaryage/devtools "0.8.1"]
                                 [figwheel-sidecar "0.5.4-7"]
                                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.14.0"]
                                 [lein-doo "0.1.7"]
                                 [lein-figwheel "0.5.4-7"]
                                 [org.clojure/clojurescript "1.9.225"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :compiler
                     {:main "xpertview.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}}}
                  
                  
                  
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/dev/resources" "env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "xpertview.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  
                  }
   :profiles/dev {}
   :profiles/test {}})
