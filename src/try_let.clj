(ns try-let
	(:gen-class))

(defmacro try-let
	[bindings & body]
	(assert (even? (count bindings))
		"try-let needs an even number of forms in binding vector")
	(let [bindings-destructured (destructure bindings)
	      bindings-ls (take-nth 2 bindings-destructured)
	      gensyms (take (count bindings-ls) (repeatedly gensym))
	      {thens nil catches 'catch finallies 'finally} (group-by #(when (and (list? %) (#{'catch 'finally} (first %))) (first %)) body)]
		`(let [[ok# ~@gensyms]
				(try
					(let [~@bindings-destructured] [true ~@bindings-ls])
					~@(map
						(fn [stanza]
							(assert (>= (count stanza) 3)
								"Malformed catch stanza")
							(let [[x y z & body] stanza]
								`(~x ~y ~z [false (do ~@body)])))
						catches)
					~@finallies)] ; only one is permitted but let the compiler handle that
			(if ok#
				(let [~@(interleave bindings-ls gensyms)]
					~@thens)
				~(first gensyms)))))

(defmacro try+-let
	[bindings & body]
	(assert (even? (count bindings))
		"try-let needs an even number of forms in binding vector")
	(let [bindings-destructured (destructure bindings)
	      bindings-ls (take-nth 2 bindings-destructured)
	      gensyms (take (count bindings-ls) (repeatedly gensym))
	      [thens stanzas] (split-with #(not (and (list? %) (= (first %) 'catch))) body)]
		`(let [[ok# ~@gensyms]
				(slingshot.slingshot/try+
					(let [~@bindings-destructured] [true ~@bindings-ls])
					~@(map
						(fn [stanza]
							(assert (>= (count stanza) 3)
								"Malformed stanza")
							(let [[x y z & body] stanza]
								(assert (= x 'catch)
									"Only catch stanzas are allowed")
								`(~x ~y ~z [false (do ~@body)])))
						stanzas))]
			(if ok#
				(let [~@(interleave bindings-ls gensyms)]
					~@thens)
				~(first gensyms)))))