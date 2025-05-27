package lunalib.lunaExtensions.scripts

import com.fs.starfarer.api.EveryFrameScript

/** Script used for some Extension Functions*/
open class EveryFrameScriptLambda(var isScriptDone: Boolean = false, var runWhilePaused: Boolean = false, var amount: Float = 0f) : EveryFrameScript {

    override fun isDone(): Boolean {
        return isScriptDone
    }

    override fun runWhilePaused(): Boolean {
        return runWhilePaused
    }

    override fun advance(amount: Float) {

    }
}