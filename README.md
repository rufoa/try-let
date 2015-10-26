try-let
=======

`try-let` is a Clojure macro designed to make handling some exceptions slightly nicer. It acts like `let`, but allows you to catch exceptions which may be thrown inside the binding vector. Exceptions thrown inside the body of the `try-let` are deliberately ignored.

[![Build Status](https://travis-ci.org/rufoa/try-let.png?branch=master)](https://travis-ci.org/rufoa/try-let)

## Installation ##

`try-let` is in Clojars. To use it in a Leiningen project, add it to your project.clj dependencies:

```clojure
[try-let "1.0.0"]
```

then require `try-let` in your code:

```xml
(ns my.example
   (:require [try-let :refer [try-let]]))
```

## Motivation ##

It can be quite difficult to combine `try/catch` with `let` properly. Clojure pushes you towards one of two patterns, neither of which is ideal.

```clojure
(try
   (let [value (func-that-throws)]
      (act-on-value value))
   (catch Exception e
      (log/error e "func-that-throws failed")))
```

In the above pattern, the scope of the `try/catch` is too great. In addition to `func-that-throws`, it also affects `act-on-value`.

```clojure
(let [value
   (try (func-that-throws)
      (catch Exception e (log/error e "func-that-throws failed")))]
   (act-on-value value))
```

In the above pattern, the scope of the `try/catch` is correct, affecting only `func-that-throws`, but when an exception is caught, `act-on-value` is evaluated regardless and must handle the exceptional case when `value` is nil.

## Use ##

With `try-let`, we can instead do:

```clojure
(try-let [value (func-that-throws)]
   (act-on-value value)
   (catch Exception e
      (log/error e "func-that-throws failed")))
```

This allows the scope of the `try/catch` to be made as precise as possible, affecting only `func-that-throws`, and for evaluation to only proceed to `act-on-value` when `value` is obtained without error. In this way, `try-let` can be thought of as similar to `if-let`, where the body is only evaluated when the value of the binding vector is not nil.

You can have multiple `catch` stanzas for different exceptions. Much of what you'd expect to work in a normal `let` works:

```clojure
(try-let [val-1 (risky-func-1) val-2 (risky-func-2 val-1)]
   (log/info "using values" val-1 "and" val-2)
   (* val-1 val-2)
   (catch SpecificException _
      (log/info "using our fallback value instead")
      123)
   (catch RuntimeException e
      (log/error e "Some other error occurred")
      (throw e)))
```

## TODO ##

- Destructuring support

## License ##

Copyright © 2015 [rufoa](https://github.com/rufoa)

Distributed under the Eclipse Public License, the same as Clojure.