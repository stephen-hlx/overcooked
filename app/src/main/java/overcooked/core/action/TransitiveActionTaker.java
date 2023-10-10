package overcooked.core.action;

import lombok.Builder;

@Builder
public class TransitiveActionTaker {
    private final ActionTemplateMaterialiser materialiser;
    private final ActionTaker actionTaker;

    public void take(TransitiveAction transitiveAction) {
        ActionDefinition materialised = materialiser.materialise(transitiveAction.getActionTemplate(),
            transitiveAction.getActionReceiver().getClass(),
            transitiveAction.getActionReceiver());

        actionTaker.take(transitiveAction.getActionPerformer(),
            materialised);
    }

}
