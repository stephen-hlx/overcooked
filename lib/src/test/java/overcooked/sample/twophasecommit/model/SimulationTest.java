package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SimulationTest {
  @Test
  void scenario1() {
    Map<Integer, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(0, WORKING);
    resourceManagerStates.put(1, WORKING);
    TransactionManagerClient simpleTransactionManagerClient =
        new SimpleTransactionManagerClient(resourceManagerStates);

    ResourceManagerClient simpleResourceManagerClient0 =
        new SimpleResourceManagerClient(0, simpleTransactionManagerClient, WORKING);
    ResourceManagerClient simpleResourceManagerClient1 =
        new SimpleResourceManagerClient(1, simpleTransactionManagerClient, WORKING);

    simpleResourceManagerClient0.prepare();
    simpleResourceManagerClient1.prepare();
    // simpleResourceManagerClient1.abort();
    simpleTransactionManagerClient.commit(simpleResourceManagerClient0);
    simpleTransactionManagerClient.commit(simpleResourceManagerClient1);
  }
}