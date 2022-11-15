package lunalib.lunaCMDs

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.LunaSettingsUI
import lunalib.lunaSettings.OpenSettingsPanelDelegate

class OpenSettingsFromCMD : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean
    {
        dialog!!.hideVisualPanel();
        dialog.hideTextPanel();

        //this is where the size of the panel is set, automatically centered
        dialog.showCustomVisualDialog(Global.getSettings().screenWidth * 0.9f,
            Global.getSettings().screenHeight * 0.9f,
            OpenSettingsPanelDelegate(LunaSettingsUI(true), dialog))

        return true
    }
}