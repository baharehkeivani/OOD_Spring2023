/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db;

import java.util.List;
import play.Environment;
import play.api.db.DBApiProvider;
import play.components.ConfigurationComponents;
import play.inject.ApplicationLifecycle;
import scala.Option;

/**
 * Java DB components. You can mix in {@link HikariCPComponents} to have a default implementation
 * for accessing a connection pool.
 *
 * <p>For example:
 *
 * <pre>
 * public class MyComponents extends BuiltInComponentsFromContext implements DBComponents, HikariCPComponents {
 *
 *      public MyComponents(ApplicationLoader.Context context) {
 *          super(context);
 *      }
 *
 *      // required methods implementations
 * }
 * </pre>
 *
 * @see ConnectionPoolComponents
 */
public interface DBComponents extends ConfigurationComponents, ConnectionPoolComponents {

  Environment environment();

  ApplicationLifecycle applicationLifecycle();

  /**
   * @return all databases associated with the {@link #dbApi()}.
   * @see DBApi#getDatabases()
   */
  default List<Database> databases() {
    return dbApi().getDatabases();
  }

  /**
   * @return the database with the given name, associated with the {@link #dbApi()}.
   * @param name the database name
   * @see DBApi#getDatabase(String)
   */
  default Database database(String name) {
    return dbApi().getDatabase(name);
  }

  default DBApi dbApi() {
    play.api.db.DBApi scalaDbApi =
        new DBApiProvider(
                environment().asScala(),
                configuration(),
                connectionPool().asScala(),
                applicationLifecycle().asScala(),
                Option.empty())
            .get();
    return new DefaultDBApi(scalaDbApi);
  }
}
