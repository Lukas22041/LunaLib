package lunalib.backend.ui.components.util

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

class TooltipHelper(var text: String, var width: Float, vararg highlights: String) :  TooltipMakerAPI.TooltipCreator
{
    var Highlights = highlights

    override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
        return false
    }

    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return width
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
        tooltip!!.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), *Highlights)
    }
}