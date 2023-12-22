# Overcooked

## Overview
Overcooked is a library that provides a way to run formal verification 
against a distributed system. It currently supports only JAVA, and it also 
requires the server and client of the system's applications to have an 
in-memory version of its implementations. It verifies the distributed system 
by exhausting the entire state space and examining whether the defined 
invariants are honoured by each of the states.

### Example
This is a simple specification of an implementation of
[two phase commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol).
Both the participants, `ResourceManager` and `TransactionManager`, have 
their server and client interfaces.

```java
// Resource Manager
interface ResourceManagerClient {
  String getId();
  void commit();
  void abort();
}

interface ResourceManagerServer {
  void prepare(TransactionManagerClient transactionManagerClient);
  void abort(TransactionManagerClient transactionManagerClient);
}

class ResourceManager implements ResourceManagerClient, ResourceManagerServer {
  // ...
}

// Transaction Manager
interface TransactionManagerClient {
  void prepare(String resourceManagerId);
  void abort(String resourceManagerId);
}

interface TransactionManagerServer {
  void abort(ResourceManagerClient resourceManagerClient);
  void commit(ResourceManagerClient resourceManagerClient);
}

class TransactionManager implements TransactionManagerClient, TransactionManagerServer {
  // ...
}
```

In production code, these interfaces will have the implementations that are
usually integrated with other services, e.g. storage. However, for model
verification, an in-memory implementation representing its internal state 
is sufficient as that makes the verification faster.

## Why is the name "Overcooked"?
When I finally got around doing this, I needed a name for it. I asked my 
wife for a name after describing what it does, "a tool that verifies that 
when a couple of actors can simultaneously perform their actions, are there 
any certain sequence of actions by certain actors that are going to cause a 
problem", she immediately recalled the video game we played at our friends' 
place, [Overcooked](https://en.wikipedia.org/wiki/Overcooked). I was like, 
"aha, that's it!".

