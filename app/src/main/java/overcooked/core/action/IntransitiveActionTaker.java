package overcooked.core.action;

import lombok.Builder;

@Builder
public class IntransitiveActionTaker {
    private final ActionTemplateMaterialiser materialiser;
    private final ActionTaker actionTaker;
    public void take(IntransitiveAction intransitiveAction) {
        ActionDefinition materialised = materialiser.materialise(intransitiveAction.getActionTemplate());

        actionTaker.take(intransitiveAction.getActor(), materialised);
    }

}
