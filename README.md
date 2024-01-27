# Overcooked

## Overview
Overcooked is a library that provides a way to run formal verification 
against a distributed system. It currently supports only JAVA, and it also 
requires the service and client of the system's applications to have an 
in-memory version of its implementations. It verifies the distributed system 
by exhausting the entire state space and examining whether the defined 
invariants are honoured by each of the states.

### Example
This is a simple specification of the
[Two Phase Commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol).

```java
// Resource Manager
interface ResourceManagerClient {
  String getId();
  void commit();
  void abort();
}

interface ResourceManagerService {
  void prepare(TransactionManagerClient transactionManagerClient);
  void abort(TransactionManagerClient transactionManagerClient);
}

class ResourceManager implements ResourceManagerClient, ResourceManagerService {
  // ...
}

// Transaction Manager
interface TransactionManagerClient {
  void prepare(String resourceManagerId);
  void abort(String resourceManagerId);
}

interface TransactionManagerService {
  void abort(ResourceManagerClient resourceManagerClient);
  void commit(ResourceManagerClient resourceManagerClient);
}

class TransactionManager implements TransactionManagerClient, TransactionManagerService {
  // ...
}
```

## How to use it?
This library is a tool to conduct formal verification.
It requires the system to be verified be implemented in a certain way in order
for it to work effectively.

More specifically, you will need:
- an in-memory implementation of each of the actors in the system
- an implementation to extract the local state of each actors
- an implementation to reconstruct the actors using their local states

And on top of that, you will:
- specify the actions that are going to take place between these actors
- specify the invariants of the system using the schema of the local states

Take the Two Phase Commit sample above, it has an in-memory implementation for
both the client and service of the `ResourceManager`
- [InMemoryResourceManagerClient](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryResourceManagerClient.java)
- [InMemoryResourceManager](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryResourceManager.java)

as well as the `TransactionManager`:
- [InMemoryTransactionManagerClient](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryTransactionManagerClient.java)
- [InMemoryTransactionManager](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryTransactionManager.java)

For examples of the specifications of actions and invariants of the Two Phase
Commit sample, please see
[modelverifier package](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier).

## How does it work?
A distributed system is said to be in a correct state if all its invariants
are honoured.

In a distributed system, there are usually multiple actors. Their interactions
form a number of interleaving. This library exhausts all possible interleaving
and verify that all of them leave the system in a state that with all its
invariants honoured.

In the rest of this section, the Two Phase Commit is frequently used as an
example.

### Actor and Action
A distributed system consists of more than one actor.
The system works by these actors interacting with each other.

In the Two Phase Commit example, there are two types of actors in the system,
`ResourceManager` and `TransactionManager`.

The interactions between these two participants are: \
![RmTmInteraction](doc/resource_manager_transaction_manager_interactions.svg)

### Local State
The local state of an actor represents the state of an individual actor.

An actor has its behaviours. It can perform an action on its own, and it can
also perform an action against a different actor. Each of these actions may
or may not transition the action's performer and receiver into a different
state, which can be described via a finite state machine.

The `ResourceManager` for example, has its own finite state machine:\
![ResourceManagerFSM](doc/resource_manager_fsm.svg)

### Global State
A distributed system is made up of a number of actors. The system's state is
therefore a collection of the states of the actors. This collection is called
`GlobalState`:

![GlobalStateExample](doc/global_state_example.svg)

### Invariant
A distributed system's correctness is defined by the upholding its invariants.
Its invariants are defined by its global states satisfying a set of
conditions. For instance, the Two Phase Commit has an invariant requiring that
if any of the ResourceManagers is `ABORTED`, no ResourceManager can be in a
state of `COMMITTED`. Therefore, the following global state would be violating
such an invariant:
![GlobalStateExample2](doc/global_state_example_2.svg)

Other invariant examples are like, TransactionManager should always have a
view of the ResourceManager's states that is consistent with all
ResourceManagers' states.

### In-Memory Implementations
The actors in the system interact with each other via the counterpart's
client, of which the implementation could be REST, gRPC, etc. The model
verification however, needs an in-memory implementation of these clients for
simulating their interactions.

It is easy to understand that the client can have an interface because an
in-memory implementation makes it easy to test the usage of the client, e.g.
using it as an in-memory mock. On the other hand, the in-memory service
implementation, like the `TransactionManagerService`, makes it easy for the
model verification to restore the state of the service using local states.

Both the participants, `ResourceManager` and `TransactionManager`, implement
their service and client interfaces. In production code, these interfaces
will have the implementations that are usually integrated with other services,
e.g. storage. However, for model verification, an in-memory implementation
representing its state is required as that allows the reconstruction of the
actors.

![OvercookedCodeStructure](doc/overcooked.svg)

For example, in production code, `ResourceManagerService` would use the
`TransactionManagerClient` to let `TransactionManagerService` know that it is
prepared for commit:
```java
class ResourceManagerService {
  private final ResourceManager resourceManager;
  private final TransactionManagerClient transactionManagerClient;
  void processRequest(Request request) {
    if (shouldPrepareForCommit(request)) {
      resourceManager.prepare(transactionManagerClient);
    }
  }
}
```
But in model verification, both the service and the client are encapsulated
in an Actor persona:
```java
class ResourceManagerActor implements ResourceManagerClient, ResourceManager { }
class TransactionManagerActor implements TransactionManagerClient, TransactionManager { }
```
and the interaction can be represented as:
```
resourceManagerActor.prepare(transactionManagerActor);
```
or more specifically:
```
ActionTemplate.<ResourceManagerActor, TransactionManagerActor>builder()
    .actionPerformerId(actionPerformerId)
    .actionType(new TransitiveActionType(TM))
    .actionLabel("prepare")
    .action(ResourceManagerActor::prepare)
    .build()
```

## Async vs Sync

## Why is the name "Overcooked"?
When I finally got around doing this, I needed a name for it. I asked my 
wife for a name after describing what it does, "a tool that verifies that 
when a couple of actors can simultaneously perform their actions, are there 
any certain sequence of actions by certain actors that are going to cause a 
problem", she immediately recalled the video game we played at our friends' 
place, [Overcooked](https://en.wikipedia.org/wiki/Overcooked). I was like, 
"aha, that's it!".

