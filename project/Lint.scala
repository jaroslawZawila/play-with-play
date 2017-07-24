import sbt.Keys._
import sbt.{Defaults, _}

object Lint {
  // A configuration which is like 'compile' except it performs additional static analysis.
  // Execute static analysis via `lint:compile`
  private val LintTarget = config("lint").extend(Compile)

  val addMainSourcesToLintTarget = {
    inConfig(LintTarget) {
      // I posted http://stackoverflow.com/questions/27575140/ and got back the bit below as the magic necessary
      // to create a separate lint target which we can run slow static analysis on.
      Defaults.compileSettings ++ Seq(
        sources in LintTarget := {
          val lintSources = (sources in LintTarget).value
          lintSources ++ (sources in Compile).value.filterNot(_.getPath.contains("routes"))          
        }
      )
    }
  }

  val addSlowAndSuperStrictScalacSwitchesToLintTarget = {
    inConfig(LintTarget) {
      // In addition to everything we normally do when we compile, we can add additional scalac switches which are
      // normally too time consuming to run.
      scalacOptions in LintTarget ++= Seq(
        // As it says on the tin, detects unused imports. This is too slow to always include in the build.
        //"-Ywarn-unused-import",
        //This produces errors you don't want in development, but is useful.
        "-Ywarn-dead-code",
        // This proved to be too much for some people, so moving it here
        "-Xfatal-warnings"
      )
    }
  }

  val addWartRemoverToLintTarget = {
    import wartremover._
    inConfig(LintTarget) {
      // TODO: increase number of warts (Warts.unsafe ?)
      wartremoverErrors ++= Seq(
        // Wart.Any,
        // Wart.AsInstanceOf,
        // Wart.NonUnitStatements,
        // Wart.Nothing,
        Wart.Any2StringAdd,
        Wart.EitherProjectionPartial,
        Wart.Enumeration,
        Wart.FinalCaseClass,
        Wart.FinalVal,
        Wart.ImplicitConversion,
        Wart.IsInstanceOf,
        Wart.JavaConversions,
        Wart.LeakingSealed,
        Wart.ListOps,
        // Wart.MutableDataStructures,
        //Wart.Null, won't work with avro macros...
        Wart.Option2Iterable,
        Wart.OptionPartial,
        Wart.Overloading,
        Wart.Product,
        Wart.Return,
        Wart.Serializable,
        Wart.ToString,
        //Wart.TryPartial,
        Wart.Var
        // Wart.While
      )
    }

  }

  val removeWartRemoverFromCompileTarget = {
    // WartRemover's sbt plugin calls addCompilerPlugin which always adds directly to the Compile configuration.
    // The bit below removes all switches that could be passed to scalac about WartRemover during a non-lint compile.
    scalacOptions in Compile := (scalacOptions in Compile).value filterNot { switch =>
      switch.startsWith("-P:wartremover:") ||
        "^-Xplugin:.*/org[.]brianmckenna/.*wartremover.*[.]jar$".r.pattern.matcher(switch).find
    }
  }

  val settings: Seq[Setting[_]] = {
    addMainSourcesToLintTarget ++
    addSlowAndSuperStrictScalacSwitchesToLintTarget ++
    addWartRemoverToLintTarget ++
    removeWartRemoverFromCompileTarget
  }

}
