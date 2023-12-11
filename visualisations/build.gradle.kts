import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "net.markjfisher"
version = "1.0.0"
val archiveBaseName = "vis-20XX"

val kotlinxCoroutineVersion: String by project

val mathsToolKitVersion: String by project
val reflectionsVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project

val lwjglVersion: String by project
val glmVersion: String by project
val glnVersion: String by project
val jomlVersion: String by project
val unoVersion: String by project

val lwjglNatives = "natives-" + if (Os.isFamily(Os.FAMILY_WINDOWS)) "windows" else "linux"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutineVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    implementation("com.marcinmoskala:DiscreteMathToolkit:$mathsToolKitVersion")
    implementation("org.reflections:reflections:$reflectionsVersion")

    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")


    // Graphics!
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
//    implementation("org.lwjgl", "lwjgl-assimp")
//    implementation("org.lwjgl", "lwjgl-bgfx")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nanovg")
//    implementation("org.lwjgl", "lwjgl-nuklear")
//    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-par")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-jemalloc")
//    implementation("org.lwjgl", "lwjgl-vulkan")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)

    // https://github.com/kotlin-graphics/imgui/issues/183
    implementation("kotlin.graphics:imgui-core:1.79+05")
    implementation("kotlin.graphics:imgui-gl:1.79+05")
    implementation("kotlin.graphics:imgui-glfw:1.79+05")

    // additional libs
    implementation("kotlin.graphics:gln:$glnVersion")
    implementation("kotlin.graphics:glm:$glmVersion")
    implementation("kotlin.graphics:uno-core:$unoVersion")
    implementation("org.joml:joml:$jomlVersion")

    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta9")

    implementation(project(":advents"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "17"
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
}