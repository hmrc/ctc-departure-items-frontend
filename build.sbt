import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "ctc-departure-items-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafmtOnCompile := true

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(A11yTest)
  .settings(inConfig(A11yTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings): _*)
  .settings(headerSettings(A11yTest): _*)
  .settings(automateHeaderSettings(A11yTest))
  .settings(
    name := appName,
    RoutesKeys.routesImport ++= Seq("models._"),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "models.Mode",
      "views.html.helper.CSPNonce",
      "viewmodels.{InputSize, LabelSize, LegendSize}",
      "templates._",
      "views.utils.ViewUtils._"
    ),
    PlayKeys.playDefaultPort := 10127,
    libraryDependencies ++= AppDependencies(),
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=html/.*:s",
    ),
    Concat.groups := Seq(
      "javascripts/application.js" -> group(Seq("javascripts/app.js"))
    ),
    uglifyCompressOptions := Seq("unused=false", "dead_code=false", "warnings=false"),
    Assets / pipelineStages := Seq(digest, concat, uglify),
    ThisBuild / useSuperShell := false,
    uglify / includeFilter := GlobFilter("application.js")
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    libraryDependencies ++= AppDependencies.test,
    DefaultBuildSettings.itSettings()
  )
