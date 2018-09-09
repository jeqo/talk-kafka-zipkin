# Lab 1: Hello world distributed tracing

This lab will introduce initial concepts about distributed tracing like Span,
Trace and Context Propagation.

## Scenario 01: Hello World Services

There is a service called Hello World that is capable of say hi in different
languages, by using a Hello Translation service, and return a response to
a user.

```
+--------+   +---------------+   +------------------+
| Client |-->| Hello Service |-->| Hello Transation |
+--------+   +---------------+   +------------------+
```

## Scenario 02: Hello World Events

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


