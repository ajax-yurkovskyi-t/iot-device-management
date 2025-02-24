plugins {
    kotlin("jvm")
    id("io.github.gw-kit.delta-coverage")
}

deltaCoverageReport {
    val targetBranch = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/main"
    diffSource.byGit {
        useNativeGit = true
        compareWith(targetBranch)
    }

    reportViews {
        val test by getting {
            violationRules.failIfCoverageLessThan(0.6)
        }
    }
    reports {
        html = true
        markdown = true
    }
}
