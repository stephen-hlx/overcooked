package overcooked.core.action;

import lombok.*;

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
