/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package javaguide.akka.factorymodules;

import javaguide.akka.*;

// #factorybinding
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

public class MyModule extends AbstractModule implements AkkaGuiceSupport {
  @Override
  protected void configure() {
    bindActor(ParentActor.class, "parent-actor");
    bindActorFactory(ConfiguredChildActor.class, ConfiguredChildActorProtocol.Factory.class);
  }
}
// #factorybinding
