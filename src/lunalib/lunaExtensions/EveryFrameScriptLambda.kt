package lunalib.lunaExtensions

import com.fs.starfarer.api.EveryFrameScript

/** Script used for some Extension Functions*/
open class EveryFrameScriptLambda(var isScriptDone: Boolean = false, var runWhilePaused: Boolean = false) : EveryFrameScript {

    override fun isDone(): Boolean {
        return isScriptDone
    }

    override fun runWhilePaused(): Boolean {
        return runWhilePaused
    }

    override fun advance(amount: Float) {

    }
}