# Overcooked

## Overview
Overcooked is a library that provides a way to run formal verification 
against a distributed system. It currently supports only JAVA, and it also 
requires the service and the client of the system's applications to have an 
in-memory version of its implementations. It verifies the distributed system 
by exhausting the entire state space and examining whether the defined
invariants are honoured by each of the states.

### Example
This is an example based on the
[Two Phase Commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol)
distributed transaction protocol.

#### Interface of the system
```java
// Resource Manager
interface ResourceManagerClient {
  void commit();
  void abort();
}

interface ResourceManager {
  void prepare(TransactionManagerClient transactionManagerClient);
  void selfAbort(TransactionManagerClient transactionManagerClient);
}

// Transaction Manager
interface TransactionManagerClient {
  void prepare(String resourceManagerId);
  void abort(String resourceManagerId);
}

interface TransactionManager {
  void abort(ResourceManagerClient resourceManagerClient);
  void commit(ResourceManagerClient resourceManagerClient);
}
```

#### Model verification
```java
class ResourceManagerActor implements ResourceManagerClient, ResourceManager { }
class TransactionManagerActor implements TransactionManagerClient, TransactionManager { }
class TwoPhaseCommitModelVerifier {
  void run() {
    ModelVerifier modelVerifier = ModelVerifier.builder()
        .actorActionConfig(actorActionConfig())
        .actorStateTransformerConfig(actorStateTransformerConfig())
        .invariantVerifier(new TransactionStateVerifier())
        .build();

    StateMachineExecutionContext stateMachineExecutionContext =
        modelVerifier.runWith(initialGlobalState());
  }
}
```

## How to use it?
This library is a tool to conduct formal verification.
It requires the system to be verified be implemented in a certain way in order
for it to work effectively.
You will need:
- an in-memory implementation of each of the actors in the system
- an implementation to extract the local state of each actors
- an implementation to reconstruct the actors using their local states
- specifications of the actions that are going to take place between these
actors
- definition of the invariants of the system using the local states

The [Two Phase Commit sample](sample/src/main/java/overcooked/sample/twophasecommit)
has an in-memory implementation for
both the client and service of the `ResourceManager`
- [InMemoryResourceManagerClient](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryResourceManagerClient.java)
- [InMemoryResourceManager](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryResourceManager.java)

as well as the `TransactionManager`:
- [InMemoryTransactionManagerClient](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryTransactionManagerClient.java)
- [InMemoryTransactionManager](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier/InMemoryTransactionManager.java)

For action specifications and invariants of the Two Phase Commit sample,
please see its
[modelverifier package](sample/src/main/java/overcooked/sample/twophasecommit/modelverifier).

The output of the verification includes the shortest paths from the initial
state to each of the invariant violating states (this shows what sequence of
actions are going to put the system in a state that violates the invariants).

For instance, the [waterjar](sample/src/main/java/overcooked/sample/waterjar)
sample has 2 states violating the invariant. And the shortest path from
the initial state to one of the violating states is \
(compared to "Two Phase Commit", the "waterjar" sample has invariant violating
states and a relatively smaller state space, making it easier to display)
![waterjar_shortest_path](doc/waterjar_failure_0.svg)

## How does it work?
A distributed system is said to be in a correct state if all its invariants
are honoured.

In a distributed system, there are usually multiple actors. Their interactions
form a number of sequences, with different interleaving. This library exhausts
sequences of all possible interleaving and verify that all of them leave the
system in a state that with all its invariants honoured.

This library has a couple of key components:

- [Actor and Action](#actor-and-action)
- [Local State](#local-state)
- [Global State](#global-state)
- [Invariant](#invariant)
- [In-Memory Implementation](#in-memory-implementation)

In the rest of this section, the Two Phase Commit sample is frequently used as
an example.

### Actor and Action
A distributed system consists of more than one actor.
The system works by these actors interacting with each other.

In the Two Phase Commit example, there are two types of actors in the system,
`ResourceManager` and `TransactionManager`.

The interactions between these two actors are: \
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
A distributed system's correctness is defined by the upholding of its
invariants. Its invariants are defined by its global states satisfying a set of
conditions. For instance, one of the Two Phase Commit protocol's invariants
requires that if any of the ResourceManagers is `ABORTED`, no ResourceManager
can be in a state of `COMMITTED`. Therefore, the following global state would
be violating such an invariant:
![GlobalStateExample2](doc/global_state_example_2.svg)

Other invariant examples are like, TransactionManager should always have a
view of the ResourceManager's states that is consistent with all
ResourceManagers' states.

### In-Memory Implementation
The actors in the system interact with each other via the counterpart's
client, of which the implementation could be REST, gRPC, etc. The model
verification however, needs an in-memory implementation of these clients for
simulating their interactions.

It is easy to understand that the client can have an interface because an
in-memory implementation makes it easy to test the usage of the client, e.g.
using it as an in-memory mock. On the other hand, the in-memory service
implementation, like the `TransactionManager`, makes it possible for the model
verification to simulate actions between the actors and also easier to restore
the state of the service using local states.

Both the actors, `ResourceManagerActor` and `TransactionManagerActor`,
implement their service and client interfaces. In production code, these
interfaces will have the implementations that are usually integrated with
other services, e.g. storage. However, for model verification, an in-memory
implementation representing its state is required as that allows the
reconstruction of the actors.

![OvercookedCodeStructure](doc/overcooked.svg)

For example, in production code, `ResourceManager` would use the
`TransactionManagerClient` to let the `TransactionManager` know that it is
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
or more specifically, as per the model verification's requirement:
```
ActionTemplate.<ResourceManagerActor, TransactionManagerActor>builder()
    .actionPerformerId(actionPerformerId)
    .actionType(new TransitiveActionType(TM))
    .actionLabel("prepare")
    .action(ResourceManagerActor::prepare)
    .build()
```

## Origin of this project
This library was inspired by the thesis
"[Stefanescu, 2006] Stefanescu, A. (2006). Automatic Synthesis of Distributed
Transition Systems. PhD thesis, Universitat Stuttgart." and by a former project
at work. The former project was about a data pipeline that consists of multiple
data sources funnelling into a single component. Back then we were not able to
verify whether the design and implementation had a flaw due to the large number
of possible interleaving of events happening between the components. Hence the
conception of this library.

The samples included in this library were inspired by the
[examples of TLA+](https://lamport.azurewebsites.net/video/videos.html).

## Why is the name "Overcooked"?
When I finally got around doing this, I needed a name for it. I asked my 
wife for a name after describing what it does, "a tool that verifies that 
when a couple of actors can simultaneously perform their actions, are there 
any certain sequence of actions by certain actors that are going to cause a 
problem", she immediately recalled the video game we played at our friends' 
place, [Overcooked](https://en.wikipedia.org/wiki/Overcooked). I was like, 
"aha, that's it!".
