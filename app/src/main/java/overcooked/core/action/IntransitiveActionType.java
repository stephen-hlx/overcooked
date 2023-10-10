package overcooked.core.action;

public class IntransitiveActionType implements ActionType {
    @Override
    public boolean isTransitive() {
        return false;
    }
}
