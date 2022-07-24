// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)

        classpath(libs.versionsPlugin)
        classpath(libs.ktlintPlugin)
        classpath(libs.spotless.gradlePlugin)
    }
}

subprojects {
    apply(plugin ="com.github.ben-manes.versions")
    apply(plugin ="org.jlleitschuh.gradle.ktlint")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
