/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package javaguide.forms;

// #register-formatter
import com.google.inject.AbstractModule;
import play.data.format.Formatters;

public class FormattersModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(Formatters.class).toProvider(FormattersProvider.class);
  }
}
// #register-formatter
