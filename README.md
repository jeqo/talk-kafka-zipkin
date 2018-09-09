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
               |          |    +--------------+
               |          |--->| DB Connector |
               |          |    +--------------+
               |          |
               |          |    +--------------+
               |          |--->| Consumer App |
               |          |    +--------------+
               +----------+
```