(defproject try-let "1.1.0"
	:description "Better exception handling for Clojure let expressions"
	:url "https://github.com/rufoa/try-let"
	:license
		{:name "Eclipse Public License"
		 :url "http://www.eclipse.org/legal/epl-v10.html"}
	:dependencies [[org.clojure/clojure "1.7.0"]]
	:profiles
		{:dev
			{:dependencies
				[[midje "1.7.0"]
				 [slingshot "0.12.2"]]
			 :plugins
				[[lein-midje "3.2"]]}}
	:main try-let)