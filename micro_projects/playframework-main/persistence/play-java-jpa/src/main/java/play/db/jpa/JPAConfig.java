/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db.jpa;

import java.util.Set;

/** JPA configuration. */
public interface JPAConfig {

  Set<PersistenceUnit> persistenceUnits();

  class PersistenceUnit {
    public String name;
    public String unitName;

    public PersistenceUnit(String name, String unitName) {
      this.name = name;
      this.unitName = unitName;
    }
  }
}
