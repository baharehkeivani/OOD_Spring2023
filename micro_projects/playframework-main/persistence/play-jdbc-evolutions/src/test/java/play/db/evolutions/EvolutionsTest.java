/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db.evolutions;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.*;
import play.db.Database;
import play.db.Databases;

public class EvolutionsTest {
  private Database database;
  private Connection connection;

  @Test
  public void testEvolutions() throws Exception {
    Evolutions.applyEvolutions(
        database, Evolutions.fromClassLoader(this.getClass().getClassLoader(), "evolutionstest/"));

    // Ensure evolutions were applied
    ResultSet resultSet = executeStatement("select * from test");
    assertTrue(resultSet.next());

    Evolutions.cleanupEvolutions(database);
    try {
      // Ensure tables don't exist
      executeStatement("select * from test");
      fail("SQL statement should have thrown an exception");
    } catch (SQLException se) {
      // pass
    }
  }

  private ResultSet executeStatement(String statement) throws Exception {
    return connection.prepareStatement(statement).executeQuery();
  }

  @Before
  public void createDatabase() {
    database = Databases.inMemory();
    connection = database.getConnection();
  }

  @After
  public void shutdown() {
    database.shutdown();
    database = null;
  }
}
