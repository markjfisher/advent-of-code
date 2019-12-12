plugins {
    kotlin("jvm") version "1.3.60"
}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.6.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
    ":advent2019:clean", ":advent2019:build"
)

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

}
