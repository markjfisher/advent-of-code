package advents.ui

data class CameraOptions(
    var cameraFrameNumber: Int,
    var maxCameraFrames: Int,
    var movingCamera: Boolean,
    var loopCamera: Boolean,
    var currentCameraPath: Int,
    val cameraPathNames: List<String>,
    var lookAhead: Int,
    var fov: Float
)
