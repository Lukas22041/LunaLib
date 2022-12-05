package lunalib.lunaDebug

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI

abstract class DebugTab(var panel: CustomPanelAPI, var dialog: InteractionDialogAPI)
{

    var pWidth = panel.position.width
    var pHeight = panel.position.height
    var panelsToOutline: MutableList<CustomPanelAPI> = ArrayList()


    abstract var panelName: String

    abstract fun init()

    abstract fun advance(amount: Float)

    abstract fun reset()

    abstract fun close()


}