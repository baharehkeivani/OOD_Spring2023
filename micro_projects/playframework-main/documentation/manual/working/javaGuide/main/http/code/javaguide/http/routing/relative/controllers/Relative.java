/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

// #relative-controller
// ###replace: package controllers;
package javaguide.http.routing.relative.controllers;

import play.*;
import play.mvc.*;

public class Relative extends Controller {

  public Result helloview(Http.Request request) {
    // ###replace:         ok(views.html.hello.render("Bob", request));
    return ok(javaguide.http.routing.relative.views.html.hello.render("Bob", request));
  }

  public Result hello(String name) {
    return ok("Hello " + name + "!");
  }
}
