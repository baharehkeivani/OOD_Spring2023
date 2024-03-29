/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db.jpa;

import play.components.ConfigurationComponents;
import play.db.DBComponents;
import play.inject.ApplicationLifecycle;

/** Java JPA Components. */
public interface JPAComponents extends DBComponents, ConfigurationComponents {

  ApplicationLifecycle applicationLifecycle();

  default JPAConfig jpaConfig() {
    return new DefaultJPAConfig.JPAConfigProvider(config()).get();
  }

  default JPAApi jpaApi() {
    return new DefaultJPAApi.JPAApiProvider(jpaConfig(), applicationLifecycle(), dbApi()).get();
  }
}
