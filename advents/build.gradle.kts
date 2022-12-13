import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "net.markjfisher"
version = "1.0.0"
val archiveBaseName = "advent-20XX"

val kotlinxCoroutineVersion: String by project

val mathsToolKitVersion: String by project
val reflectionsVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project

val jomlVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutineVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    implementation("com.marcinmoskala:DiscreteMathToolkit:$mathsToolKitVersion")
    implementation("org.reflections:reflections:$reflectionsVersion")
    implementation("org.joml:joml:$jomlVersion")

    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks {
    "build" {
        dependsOn(shadowJar)
    }

    register("advent2019", JavaExec::class) {
        mainClass.set("net.fish.y2019.AdventOfCode2019")
        classpath = sourceSets["main"].runtimeClasspath
    }

    register("advent2020", JavaExec::class) {
        mainClass.set("net.fish.y2020.AdventOfCode2020")
        classpath = sourceSets["main"].runtimeClasspath
    }

    register("advent2021", JavaExec::class) {
        mainClass.set("net.fish.y2021.AdventOfCode2021")
        classpath = sourceSets["main"].runtimeClasspath
    }

    register("advent2022", JavaExec::class) {
        mainClass.set("net.fish.y2022.AdventOfCode2022")
        classpath = sourceSets["main"].runtimeClasspath
    }

    register("advent2022day12", JavaExec::class) {
        mainClass.set("net.fish.y2022.Advent2022Day12")
        classpath = sourceSets["main"].runtimeClasspath
    }

    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
            freeCompilerArgs += listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

//    withType<ShadowJar> {
//        // <WORKAROUND for="https://github.com/johnrengelman/shadow/issues/448">
//        configurations = listOf(
//            project.configurations.implementation.get(),
//            project.configurations.runtimeOnly.get()
//        ).onEach { it.isCanBeResolved = true }
//        // </WORKAROUND>
//    }
}