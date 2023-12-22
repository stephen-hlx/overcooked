package overcooked.sample.twophasecommit.modelverifier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class RefCell<DataT> {
  private DataT data;
}
