# sparktimus-prime
A small demonstration prime number sieve using Spark and Sparkling
(Clojure Bindings for Spark)

## Running stuff

Just use standard lein running commands i.e. `lein run 100` to get all
the primes up to 100.

By default there is a fair amount of logging to stderr, so you can see
what Spark is doing.

Here is an example using a different log4j configuration (included).

```
$ JAVA_OPTS=-Dlog4j.configuration=file:resources/log4j.warn.properties lein run 100
15/10/15 13:35:58 WARN main util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
15/10/15 13:36:00 WARN main metrics.MetricsSystem: Using default name DAGScheduler for source because spark.app.id is not set.
15/10/15 13:36:04 DEBUG main sparktimus-prime.core: pass 0: [2]                 
15/10/15 13:36:04 DEBUG main sparktimus-prime.core: pass 1: [3]
15/10/15 13:36:04 DEBUG main sparktimus-prime.core: pass 2: [5 7]
15/10/15 13:36:04 DEBUG main sparktimus-prime.core: pass 3: [11 13 17 19 23 29 31 37 41 43 47]
15/10/15 13:36:04 DEBUG main sparktimus-prime.core: remainder: [53 59 61 67 71 73 79 83 89 97]
2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97
```

## Packaging stuff

To run this standalone, you want to use `lein uberjar`.

Example of building and running:

```
$ lein uberjar
Release versions may not depend upon snapshots. 
Freeze snapshots to dated versions or set the LEIN_SNAPSHOTS_IN_RELEASE environment variable to override.
zalando-08182:~/projects/sparktimus-prime $ lein uberjar
Warning: specified :main without including it in :aot. 
Implicit AOT of :main will be removed in Leiningen 3.0.0. 
If you only need AOT for your uberjar, consider adding :aot :all into your
:uberjar profile instead.
Compiling clojure.tools.logging
Compiling clojure.tools.logging.impl
Compiling sparktimus-prime.core
Compiling sparkling.serialization
Compiling sparkling.destructuring
Compiling sparkling.utils
Compiling sparkling.core
Compiling sparktimus-prime.core
Created /Users/hkelly/projects/sparktimus-prime/target/uberjar+uberjar/sparktimus-prime-1.0.0-SNAPSHOT.jar
Created /Users/hkelly/projects/sparktimus-prime/target/uberjar/sparktimus-prime-1.0.0-SNAPSHOT-standalone.jar

$ java -Dlog4j.configuration=file:resources/log4j.warn.properties -jar target/uberjar/sparktimus-prime-1.0.0-SNAPSHOT-standalone.jar 100
15/10/15 16:08:45 WARN main util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
15/10/15 16:08:47 WARN main metrics.MetricsSystem: Using default name DAGScheduler for source because spark.app.id is not set.
15/10/15 16:08:52 DEBUG main sparktimus-prime.core: pass 0: [2]                 
15/10/15 16:08:52 DEBUG main sparktimus-prime.core: pass 1: [3]
15/10/15 16:08:52 DEBUG main sparktimus-prime.core: pass 2: [5 7]
15/10/15 16:08:52 DEBUG main sparktimus-prime.core: pass 3: [11 13 17 19 23 29 31 37 41 43 47]
15/10/15 16:08:52 DEBUG main sparktimus-prime.core: remainder: [53 59 61 67 71 73 79 83 89 97]
2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97

```


