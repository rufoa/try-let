(defproject try-let "1.3.1-SNAPSHOT"
	:description "Better exception handling for Clojure let expressions"
	:url "https://github.com/rufoa/try-let"
	:license
		{:name "Eclipse Public License"
		 :url "http://www.eclipse.org/legal/epl-v10.html"}
	:dependencies
		[[org.clojure/clojure "1.10.0"]
		 [org.clojure/core.specs.alpha "0.2.44"]]
	:profiles
		{:dev
			{:dependencies
				[[midje "1.9.6"]
				 [slingshot "0.12.2"]]
			 :plugins
				[[lein-midje "3.2"]]}}
	:scm
		{:name "git"
		 :url "https://github.com/rufoa/try-let"}
	:main try-let)