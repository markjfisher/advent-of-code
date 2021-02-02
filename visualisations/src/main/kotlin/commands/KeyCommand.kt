package commands

sealed class KeyCommand
sealed class SingleKeyPressCommand: KeyCommand() {
    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

object DecreaseSpeed: SingleKeyPressCommand()
object IncreaseSpeed: SingleKeyPressCommand()
object TogglePause: SingleKeyPressCommand()
object ResetGame: SingleKeyPressCommand()
object SingleStep: SingleKeyPressCommand()
object PrintState: SingleKeyPressCommand()
object ResetCamera: SingleKeyPressCommand()
object RunCamera: SingleKeyPressCommand()
object LoadCamera: SingleKeyPressCommand()
object NextCamera: SingleKeyPressCommand()
object SetCamera: SingleKeyPressCommand()
object SetLookahead: SingleKeyPressCommand()
object ToggleMessages: SingleKeyPressCommand()
object ToggleHud: SingleKeyPressCommand()
object ToggleImgUI: SingleKeyPressCommand()
object CreateSurface: SingleKeyPressCommand()
object ToggleTexture: SingleKeyPressCommand()

sealed class MovementCommand: KeyCommand()
object MoveForward: MovementCommand()
object MoveBackward: MovementCommand()
object MoveLeft: MovementCommand()
object MoveRight: MovementCommand()
object MoveDown: MovementCommand()
object MoveUp: MovementCommand()