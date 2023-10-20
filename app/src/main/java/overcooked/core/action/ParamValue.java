package overcooked.core.action;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Value
public class ParamValue implements Param {
    Class<?> clazz;
    Object value;

    @Override
    public boolean isTemplate() {
        return false;
    }
}
