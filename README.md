# showcase_java

This is a simple repository to showcase some ideas in Java.

## Goal

The goal is not to build an application that serves a real-world purpose.
Instead, the goal is to try out different approaches and technologies.

Here are some of the concepts and technologies currently applied:

- Domain Driven Design
- Clean architecture (layers: domain, application, gateway)
- Kafka and CQRS
- REST and gRPC
- Unit tests, approval tests

## Execution

The application can be executed by running:

```bash
mvn spring-boot:run
```

The REST APIs run on `localhost:8080` while the gRPC API is on `localhost:9090`.

The application expects a Kafka instance running on `localhost:9092`.