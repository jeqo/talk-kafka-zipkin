# Talk: Tracing Kafka-based applications with Zipkin

Demo material from talk about tracing Kafka-based applications with Zipkin

## Labs

1. Hello world distributed tracing: Understand basics about distributed
   tracing.
2. Tracing Kafka-based apps: Instrumenting Kafka-based applications with
   Zipkin.

## Lab 1: Hello world distributed tracing

This lab will introduce initial concepts about distributed tracing like Span,
Trace and Context Propagation.

### Scenario 01: Hello World Services

There is a service called Hello World that is capable of say hi in different
languages, by using a Hello Translation service, and return a response to
a user.

```
+--------+   +---------------+   +------------------+
| Client |-->| Hello Service |-->| Hello Transation |
+--------+   +---------------+   +------------------+
```

#### How to run it

//TODO

### Scenario 02: Hello World Events

Instead of a web client, a Client application with implement a batch process 
to call Hello Service and produce events into a Kafka Topic.


```
+--------+   +---------------+   +------------------+
|        |-->| Hello Service |-->| Hello Transation |
|        |   +---------------+   +------------------+
| Hello  |
| Client |   +-------+   +----------------+
|        |-->| Kafka |-->| Hello Consumer |
+--------+   +-------+   +----------------+
```

#### How to run it

//TODO

## Lab 02: Twitter Kafka-based application

Data-flow view

```
+---------+   +-------------------+          +------------------+    +--------------+
| Twitter |-->| Twitter Connector |-(Kafka)->| Stream Transform |-+->| DB Connector |
+---------+   +-------------------+          +------------------+ |  +--------------+
                                                               (kafka)
                                                                  |  +--------------+
                                                                  +->| Consumer App |
                                                                     +--------------+
```

Choreography view:

```
                  Kafka
               +----------+
+---------+    |          |    +------------------+
| Twitter |--->|          |--->|                  |
+---------+    |          |    | Stream Transform |
               |          |<---|                  |
               |          |    +------------------+
               |          |
               |          |    +--------------+    +------------+
               |          |--->| DB Connector |--->| Postgresql |
               |          |    +--------------+    +------------+
               |          |
               |          |    +--------------+    +---------+
               |          |--->| Consumer App |--->| Console |
               |          |    +--------------+    +---------+
               +----------+
```

### Instrumentation

- Kafka Clients (Producer/Consumer): <https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-clients>
- Kafka Streams (WIP): <https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-streams>
- Kafka Interceptors for Kafka Connect, REST Proxy, etc (WIP): <https://github.com/sysco-middleware/kafka-interceptors/tree/master/zipkin> 

### How to run it

//TODO

## Lab 3: Spigo Simulation

### How to run it

Clone Spigo project: 

```
git clone git@github.com:jeqo/spigo
cd spigo
```

Visualize architectures on `json_arch/` dir and with UI:

```
cd ui
npm run dev
```

Go to <http://localhost:8000>

Run Spigo to generate traces:

```
spigo -c -d 5 -a netflix
```

Export traces to Zipkin:

```
misc/zipkin.sh netflix
```

Visualize traces on [Zipkin](http://localhost:9411)

Generate Vizceral JSON from Architecture:

```
git checkout git@github.com:jeqo/go-vizceral
cd go-vizceral/arch2vizceral
cp <arch json> arch_json/.
./arch2vizceral -arch netflix > netflix_vizceral.json
```

Copy Vizceral JSON to Vizceral example project and run example:

```
git checkout git@github.com:jeqo/vizceral-example
cd vizceral-example
npm run dev
```

Go to <http://localhost:8080>
