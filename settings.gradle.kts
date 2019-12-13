rootProject.name = "advent-of-code"

include(
    "advents"
)

val kotlinVersion: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.kapt" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.allopen" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.multiplatform" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.serialization" -> useVersion(kotlinVersion)
            }
        }
    }
}
