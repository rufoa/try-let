(ns try-let
	(:require
		[clojure.spec.alpha :as s]
		[clojure.core.specs.alpha :as cs]))


(s/def ::catch
	(s/and
		list?
		#(>= (count %) 3)
		#(= (first %) 'catch)))

(s/def ::else
	(s/and
		list?
		#(= (first %) 'else)))

(s/def ::finally
	(s/and
		list?
		#(= (first %) 'finally)))

(s/def ::then
	#(not (and
		(list? %)
		(#{'catch 'else 'finally} (first %)))))


(s/def ::try-let-body
	(s/alt
		:stanzas (s/cat
			:thens   (s/* ::then)
			:catches (s/* ::catch)
			:finally (s/? ::finally))
		:stanzas (s/cat
			:catches (s/* ::catch)
			:finally (s/? ::finally)
			:thens   (s/* ::then))))

(s/def ::try+-let-body
	(s/alt
		:stanzas (s/cat
			:thens   (s/* ::then)
			:catches (s/* ::catch)
			:else    (s/? ::else)
			:finally (s/? ::finally))
		:stanzas (s/cat
			:catches (s/* ::catch)
			:else    (s/? ::else)
			:finally (s/? ::finally)
			:thens   (s/* ::then))))


(s/fdef try-let
	:args (s/cat
		:bindings ::cs/bindings
		:body ::try-let-body))

(s/fdef try+-let
	:args (s/cat
		:bindings ::cs/bindings
		:body ::try+-let-body))


(defmacro try-let
	[bindings & body]
	(let [[_ {:keys [thens catches finally]}] (s/conform ::try-let-body body)
	      bindings-destructured (destructure bindings)
	      bindings-ls (take-nth 2 bindings-destructured)
	      gensyms (take (count bindings-ls) (repeatedly gensym))]
		`(let [[ok# ~@gensyms]
				(try
					(let [~@bindings-destructured] [true ~@bindings-ls])
					~@(map
						(fn [stanza]
							(let [[x y z & body] stanza]
								`(~x ~y ~z [false (do ~@body)])))
						catches)
					~@(when finally [finally]))]
			(if ok#
				(let [~@(interleave bindings-ls gensyms)]
					~@thens)
				~(first gensyms)))))

(defmacro try+-let
	[bindings & body]
	(let [[_ {:keys [thens catches else finally]}] (s/conform ::try+-let-body body)
	      bindings-destructured (destructure bindings)
	      bindings-ls (take-nth 2 bindings-destructured)
	      gensyms (take (count bindings-ls) (repeatedly gensym))]
		`(let [[ok# ~@gensyms]
				(slingshot.slingshot/try+
					(let [~@bindings-destructured] [true ~@bindings-ls])
					~@(map
						(fn [stanza]
							(let [[x y z & body] stanza]
								`(~x ~y ~z [false (do ~@body)])))
						catches)
					~@(when else [else])
					~@(when finally [finally]))]
			(if ok#
				(let [~@(interleave bindings-ls gensyms)]
					~@thens)
				~(first gensyms)))))