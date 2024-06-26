package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class TransactionStateVerifierTest {
  private static final ActorId TM = new ActorId("TM");
  private static final ActorId RM_0 = new ActorId("RM_0");
  private static final ActorId RM_1 = new ActorId("RM_1");

  private final TransactionStateVerifier verifier = new TransactionStateVerifier(TM);

  static Object[][] test_cases() {
    return new Object[][] {
        // valid,            tmState,                   rm0State,       rm1State
        // all ResourceManagers are of the same state
        {  true,  globalState(tm(ABORTED,   ABORTED),   rm0(ABORTED),   rm1(ABORTED))},
        {  true,  globalState(tm(COMMITTED, COMMITTED), rm0(COMMITTED), rm1(COMMITTED))},
        {  true,  globalState(tm(PREPARED,  PREPARED),  rm0(PREPARED),  rm1(PREPARED))},
        {  true,  globalState(tm(WORKING,   WORKING),   rm0(WORKING),   rm1(WORKING))},

        // partially aborted / committed / prepared
        {  false, globalState(tm(ABORTED,   COMMITTED), rm0(ABORTED),   rm1(COMMITTED))},
        {  true,  globalState(tm(ABORTED,   PREPARED),  rm0(ABORTED),   rm1(PREPARED))},
        {  true,  globalState(tm(ABORTED,   WORKING),   rm0(ABORTED),   rm1(WORKING))},
        {  true,  globalState(tm(COMMITTED, PREPARED),  rm0(COMMITTED), rm1(PREPARED))},
        {  false, globalState(tm(COMMITTED, WORKING),   rm0(COMMITTED), rm1(WORKING))},
        {  true,  globalState(tm(PREPARED,  WORKING),   rm0(PREPARED),  rm1(WORKING))},

        // inconsistent ResourceManager state view from TransactionManager
        {  false, globalState(tm(WORKING,   WORKING),   rm0(WORKING),   rm1(PREPARED))},
        {  false, globalState(tm(WORKING,   PREPARED),  rm0(WORKING),   rm1(WORKING))},
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  void works(boolean expectedValidity, GlobalState globalState) {
    assertThat(verifier.verify(globalState)).isEqualTo(expectedValidity);
  }

  private static GlobalState globalState(TransactionManagerActorState transactionManagerActorState,
                                         ResourceManagerActorState resourceManager0ActorState,
                                         ResourceManagerActorState resourceManager1ActorState) {
    return new GlobalState(ImmutableMap.of(
        TM, localStateOf(transactionManagerActorState),
        RM_0, localStateOf(resourceManager0ActorState),
        RM_1, localStateOf(resourceManager1ActorState)));
  }

  private static TransactionManagerActorState tm(ResourceManagerState resourceManager0State,
                                                 ResourceManagerState resourceManager1State) {
    return new TransactionManagerActorState(ImmutableMap.of(
        RM_0.getId(), resourceManager0State,
        RM_1.getId(), resourceManager1State));
  }

  private static ResourceManagerActorState rm0(ResourceManagerState resourceManagerState) {
    return rmState(RM_0, resourceManagerState);
  }

  private static ResourceManagerActorState rm1(ResourceManagerState resourceManagerState) {
    return rmState(RM_1, resourceManagerState);
  }

  private static ResourceManagerActorState rmState(ActorId resourceManagerId,
                                                   ResourceManagerState resourceManagerState) {
    return new ResourceManagerActorState(resourceManagerId.getId(), resourceManagerState);
  }

  private static LocalState localStateOf(ActorState actorState) {
    return LocalState.builder()
        .actorState(actorState)
        .build();
  }
}