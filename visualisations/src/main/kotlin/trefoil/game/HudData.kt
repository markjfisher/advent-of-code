package trefoil.game

data class HudData(
    val isPaused: Boolean,
    val flashMessage: String = "",
    val flashPercentage: Int = 0,
)
