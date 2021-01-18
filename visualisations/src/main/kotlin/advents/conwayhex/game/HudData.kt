package advents.conwayhex.game

data class HudData(
    val speed: Int,
    val iteration: Int,
    val isPaused: Boolean,
    val liveCount: Int,
    val flashMessage: String = "",
    val flashPercentage: Int = 0
)
