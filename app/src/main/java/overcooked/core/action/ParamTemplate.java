package overcooked.core.action;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Value
public class ParamTemplate<T> implements Param {
    Class<T> clazz;

    @Override
    public boolean isTemplate() {
        return true;
    }
}
