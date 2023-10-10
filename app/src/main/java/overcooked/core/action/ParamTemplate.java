package overcooked.core.action;

import lombok.*;

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
