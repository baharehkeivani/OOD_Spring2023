/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package javaguide.detailed.filters.remove;

import java.util.List;
import java.util.stream.Collectors;
import play.ApplicationLoader;
import play.BuiltInComponentsFromContext;
import play.filters.components.HttpFiltersComponents;
import play.filters.csrf.CSRFFilter;
import play.mvc.EssentialFilter;
import play.routing.Router;

// #removing-filters-compile-time-di
public class MyAppComponents extends BuiltInComponentsFromContext implements HttpFiltersComponents {

  public MyAppComponents(ApplicationLoader.Context context) {
    super(context);
  }

  @Override
  public List<EssentialFilter> httpFilters() {
    return HttpFiltersComponents.super.httpFilters().stream()
        .filter(filter -> !filter.getClass().equals(CSRFFilter.class))
        .collect(Collectors.toList());
  }

  @Override
  public Router router() {
    return Router.empty(); // implement the router as needed
  }
}
// #removing-filters-compile-time-di
