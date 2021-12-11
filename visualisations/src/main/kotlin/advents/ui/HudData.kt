package advents.ui

data class HudData(
    val speed: Int,
    val iteration: Int,
    val isPaused: Boolean,
//    val liveCount: Int,
    val flashMessage: String = "",
    val flashPercentage: Int = 0,
//    val createdCount: Int = 0,
//    val destroyedCount: Int = 0,
    val showBar: Boolean = false,
    val customData: Map<String, Long> = mutableMapOf()
)
