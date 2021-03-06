package org.seasar.doma.jdbc.criteria.context;

import java.util.Objects;

public class SubSelectContext<RESULT> {
  public final SelectContext context;

  public SubSelectContext(SelectContext context) {
    this.context = Objects.requireNonNull(context);
  }
}
