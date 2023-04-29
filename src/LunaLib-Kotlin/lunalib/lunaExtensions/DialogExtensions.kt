package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import lunalib.backend.ui.OpenCustomPanelFromDialog
import lunalib.backend.ui.settings.LunaSettingsUIMainPanel
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin

fun InteractionDialogAPI.openLunaCustomPanel(plugin: LunaBaseCustomPanelPlugin)
{
    this.showCustomVisualDialog(Global.getSettings().screenWidth * 0.8f,
        Global.getSettings().screenHeight * 0.8f,
        OpenCustomPanelFromDialog.VisualDelegate(plugin, this, true))
}

