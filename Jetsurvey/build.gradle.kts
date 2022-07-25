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
        classpath(libs.updaterPlugin)
        classpath(libs.ktlintPlugin)
        classpath(libs.spotless.gradlePlugin)
    }
}

apply(plugin ="com.github.ben-manes.versions")
apply(plugin ="nl.littlerobots.version-catalog-update")

subprojects {
    apply(plugin ="org.jlleitschuh.gradle.ktlint")
    apply(plugin ="com.diffplug.spotless")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
