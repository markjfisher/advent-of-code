package engine

import java.lang.Exception

class GameEngine(
    windowTitle: String,
    width: Int,
    height: Int,
    vSync: Boolean,
    private val targetUPS: Int = 30,
    private val gameLogic: GameLogic
) : Runnable {

    private val window: Window = Window(windowTitle, width, height, vSync)
    private val timer: Timer = Timer()
    private val mouseInput: MouseInput = MouseInput()

    companion object {
        const val TARGET_FPS = 75
    }

    override fun run() {
        try {
            init()
            gameLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cleanup()
        }
    }

    private fun init() {
        // window.init()
        window.initImgui()
        timer.set()
        mouseInput.init(window)
        gameLogic.init(window)
    }

    private fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / targetUPS

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
        mouseInput.input(window)
        gameLogic.input(window, mouseInput)
    }

    private fun update(interval: Float) {
        gameLogic.update(interval, mouseInput, window)
    }

    private fun render() {
        gameLogic.render(window)
        window.update()
    }
}