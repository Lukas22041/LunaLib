package lunalib.backend.ui.debug

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.ui.CustomPanelAPI

interface LunaDebugUIInterface : CustomUIPanelPlugin
{
    fun getTab(): String

    fun init(parentPanel: CustomPanelAPI, parentClass: LunaDebugUIMainPanel, panel: CustomPanelAPI)
}