/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db.evolutions;

import static java.util.stream.Collectors.toList;

import java.util.*;
import play.libs.Scala;
import scala.collection.Seq;

/** Reads evolutions. */
public abstract class EvolutionsReader implements play.api.db.evolutions.EvolutionsReader {
  public final Seq<play.api.db.evolutions.Evolution> evolutions(String db) {
    Collection<Evolution> evolutions = getEvolutions(db);
    if (evolutions != null) {
      List<play.api.db.evolutions.Evolution> scalaEvolutions =
          evolutions.stream()
              .map(
                  e ->
                      new play.api.db.evolutions.Evolution(
                          e.getRevision(), e.getSqlUp(), e.getSqlDown()))
              .collect(toList());
      return Scala.asScala(scalaEvolutions);
    } else {
      return Scala.asScala(Collections.emptyList());
    }
  }

  /**
   * Get the evolutions for the given database name.
   *
   * @param db The name of the database.
   * @return The collection of evolutions.
   */
  public abstract Collection<Evolution> getEvolutions(String db);
}
