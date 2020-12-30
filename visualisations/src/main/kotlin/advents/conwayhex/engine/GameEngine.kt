package advents.conwayhex.engine

import java.lang.Exception

class GameEngine(
    windowTitle: String,
    width: Int,
    height: Int,
    vSync: Boolean,
    private val gameLogic: GameLogic
) : Runnable {

    private val window: Window = Window(windowTitle, width, height, vSync)
    private val timer: Timer = Timer()

    companion object {
        const val TARGET_FPS = 75
        const val TARGET_UPS = 30
    }

    override fun run() {
        try {
            init()
            gameLoop()
        } catch (excp: Exception) {
            excp.printStackTrace()
        } finally {
            cleanup()
        }
    }

    private fun init() {
        window.init()
        timer.init()
        gameLogic.init(window)
    }

    private fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / TARGET_UPS

        val running = true
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            if (!window.isvSync()) {
                sync()
            }
        }
    }

    private fun cleanup() {
        gameLogic.cleanup()
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try { Thread.sleep(1) } catch (_: InterruptedException) {}
        }
    }

    private fun input() {
        gameLogic.input(window)
    }

    private fun update(interval: Float) {
        gameLogic.update(interval)
    }

    private fun render() {
        gameLogic.render(window)
        window.update()
    }
}