plugins {
    kotlin("jvm") apply true
}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "6.7"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
    ":advents:clean", ":advents:build"
)

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://jitpack.io")
    }

}
