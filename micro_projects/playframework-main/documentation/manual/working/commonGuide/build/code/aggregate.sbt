// Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>

//#content
lazy val common: Project = (project in file("common"))
  .enablePlugins(PlayScala)
  .settings(
    aggregateReverseRoutes := Seq(api, web)
  )

lazy val api = (project in file("api"))
  .enablePlugins(PlayScala)
  .dependsOn(common)

lazy val web = (project in file("web"))
  .enablePlugins(PlayScala)
  .dependsOn(common)
//#content

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(api, web)
