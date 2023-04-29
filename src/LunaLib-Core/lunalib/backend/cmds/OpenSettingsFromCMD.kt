package lunalib.backend.cmds

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.OpenCustomPanelFromDialog
import lunalib.backend.ui.settings.LunaSettingsUIMainPanel
import lunalib.lunaExtensions.openLunaCustomPanel


// Used to open the LunaSettingsUI from Rules.CSV
// Not part of the other rules utilities.
class OpenSettingsFromCMD : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean
    {
        dialog!!.hideVisualPanel();
        dialog.hideTextPanel();
        dialog.promptText = ""

        var scale = Global.getSettings().screenScaleMult

        /*dialog.showCustomVisualDialog(Global.getSettings().screenWidth * 0.8f,
            Global.getSettings().screenHeight * 0.8f,
            OpenCustomPanelFromDialog.VisualDelegate(LunaSettingsUIMainPanel(true), dialog, true))*/

        dialog.openLunaCustomPanel(LunaSettingsUIMainPanel(true))

        return true
    }
}