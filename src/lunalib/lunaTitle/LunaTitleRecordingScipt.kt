package lunalib.lunaTitle

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global

class LunaTitleRecordingScipt : EveryFrameScript {

    override fun isDone(): Boolean {
        return false
    }


    override fun runWhilePaused(): Boolean {
        return false
    }


    override fun advance(amount: Float) {
        var location = Global.getSector().playerFleet.containingLocation
        TitlescreenManager.lastSystemId = location.id
        TitlescreenManager.lastSystemTags = ArrayList(location.tags)
    }

}