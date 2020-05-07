package org.seasar.doma.jdbc.criteria.entity;

import java.util.Arrays;
import java.util.List;
import org.seasar.doma.jdbc.criteria.def.DefaultPropertyDef;
import org.seasar.doma.jdbc.criteria.def.EntityDef;
import org.seasar.doma.jdbc.criteria.def.PropertyDef;

public class Dept_ implements EntityDef<Dept> {

  private final _Dept entityType = new _Dept();

  public final PropertyDef<Integer> id = new DefaultPropertyDef<>(Integer.class, entityType, "id");

  public final PropertyDef<String> name =
      new DefaultPropertyDef<>(String.class, entityType, "name");

  public _Dept asType() {
    return entityType;
  }

  @Override
  public List<PropertyDef<?>> allPropertyDefs() {
    return Arrays.asList(id, name);
  }
}