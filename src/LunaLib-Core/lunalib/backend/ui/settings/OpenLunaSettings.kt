package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.ui.CustomPanelAPI


internal class OpenSettingsPanelInteraction : InteractionDialogPlugin
{

    var dialog: InteractionDialogAPI? = null

    override fun init(dialog: InteractionDialogAPI?) {
        this.dialog = dialog;
        dialog!!.hideVisualPanel();
        dialog.hideTextPanel();
        dialog.promptText = ""

        var scale = Global.getSettings().screenScaleMult

        dialog.showCustomVisualDialog(Global.getSettings().screenWidth * 0.8f,
            Global.getSettings().screenHeight * 0.8f,
            OpenSettingsPanelDelegate(LunaSettingsUIMainPanel(false), dialog))
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI>? {
        return null
    }
}

internal class OpenSettingsPanelDelegate(missionPanel: LunaSettingsUIMainPanel?, dialog: InteractionDialogAPI) : CustomVisualDialogDelegate
{

    private var callbacks: DialogCallbacks? = null
    private var plugin: LunaSettingsUIMainPanel? = null
    private var dialog: InteractionDialogAPI? = null

    init {
        this.plugin = missionPanel;
        this.dialog = dialog;
    }

    override fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?) {
        this.callbacks = callbacks;
        plugin!!.init(panel, callbacks, dialog);
    }

    override fun getCustomPanelPlugin(): CustomUIPanelPlugin? {
        return plugin
    }

    override fun getNoiseAlpha(): Float {
        return 0f
    }

    override fun advance(amount: Float) {

    }

    override fun reportDismissed(option: Int) {

    }

}