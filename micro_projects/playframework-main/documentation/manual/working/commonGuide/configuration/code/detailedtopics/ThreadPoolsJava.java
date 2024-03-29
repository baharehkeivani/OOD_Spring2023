/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package detailedtopics;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

import org.junit.Test;
import play.Application;

public class ThreadPoolsJava {

  @Test
  public void usingAppClassLoader() throws Exception {
    final Application app = fakeApplication();
    running(
        app,
        () -> {
          String myClassName = "java.lang.String";
          try {
            // #using-app-classloader
            Class myClass = app.classloader().loadClass(myClassName);
            // #using-app-classloader
            assertThat(myClass, notNullValue());
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
