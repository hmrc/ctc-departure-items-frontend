import play.core.PlayVersion
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "7.22.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.23.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "org.typelevel"     %% "cats-core"                      % "2.9.0",
    "com.chuusai"       %% "shapeless"                      % "2.3.10",
    "uk.gov.hmrc"       %% "play-allowlist-filter"          % "1.2.0",
    "org.apache.commons" % "commons-text"                   % "1.10.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"               % "3.2.15",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"  % bootstrapVersion,
    "com.typesafe.play"       %% "play-test"               % PlayVersion.current,
    "org.mockito"             %  "mockito-core"            % "5.2.0",
    "org.scalatestplus"       %% "mockito-4-6"             % "3.2.15.0",
    "org.scalacheck"          %% "scalacheck"              % "1.17.0",
    "org.scalatestplus"       %% "scalacheck-1-17"         % "3.2.15.0",
    "org.pegdown"             %  "pegdown"                 % "1.6.0",
    "org.jsoup"               %  "jsoup"                   % "1.15.4",
    "com.github.tomakehurst"  %  "wiremock-standalone"     % "2.27.2",
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.62.2",
    "io.github.wolfendale"    %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test
}
