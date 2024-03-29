/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package views.js.helper

import org.specs2.mutable.Specification

class HelpersSpec extends Specification {
  "@json" should {
    "Produce valid JavaScript strings" in {
      json("foo").toString must equalTo("\"foo\"")
    }

    "Properly escape quotes" in {
      json("fo\"o").toString must equalTo("\"fo\\\"o\"")
    }

    "Not escape HTML entities" in {
      json("fo&o").toString must equalTo("\"fo&o\"")
    }

    "Produce valid JavaScript literal objects" in {
      json(Map("foo" -> "bar")).toString must equalTo("{\"foo\":\"bar\"}")
    }

    "Produce valid JavaScript arrays" in {
      json(List("foo", "bar")).toString must equalTo("[\"foo\",\"bar\"]")
    }
  }
}
