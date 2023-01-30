package lunalib.backend.ui.settingsV2

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import lunalib.backend.scripts.LunaKeybinds
import org.lwjgl.input.Keyboard

class LunaSettingsUIMainPanel(var newGame: Boolean) : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null

    private var modsPanel: CustomPanelAPI? = null
    private var modsPanelPlugin: LunaSettingsUIModsPanel? = null

    private var settingsPanel: CustomPanelAPI? = null
    private var settingsPanelPlugin: LunaSettingsUISettingsPanel? = null

    private var width = 0f
    private var height = 0f

    fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?, dialog: InteractionDialogAPI?) {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        width = panel!!.position.width
        height = panel!!.position.height

        var element = panel.createUIElement(width, 20f, false)
        element.addSectionHeading("Mod Settings", Alignment.MID, 0f)
        element.position.inTL(0f, 0f)
        panel.addUIElement(element)

        modsPanelPlugin = LunaSettingsUIModsPanel(newGame)
        modsPanel = panel.createCustomPanel(250f, height * 0.96f, modsPanelPlugin)
        panel.addComponent(modsPanel)
        modsPanel!!.position.inTL(0f, 20f)
        modsPanelPlugin!!.init(panel, modsPanel!!)

        settingsPanelPlugin = LunaSettingsUISettingsPanel()
        settingsPanel = panel.createCustomPanel(width - 240, height * 0.96f, settingsPanelPlugin)
        panel.addComponent(settingsPanel)
        settingsPanel!!.position.inTL(240f, 20f)
        settingsPanelPlugin!!.init(panel, settingsPanel!!)

       /* pW = this.panel!!.position.width
        pH = this.panel!!.position.height

        reset()*/
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {

    }

    companion object {
        var closeCooldown = 0
    }

    override fun processInput(events: MutableList<InputEventAPI>) {

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isKeyDownEvent && event.eventValue == LunaKeybinds.settingsKeybind)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                if (!newGame) dialog!!.dismiss()

                LunaSettingsUIModsPanel.selectedMod = null
                LunaSettingsUISettingsPanel.addedElements.clear()

                closeCooldown = 30
                return@forEach
            }
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                if (!newGame) dialog!!.dismiss()

                LunaSettingsUIModsPanel.selectedMod = null
                LunaSettingsUISettingsPanel.addedElements.clear()

                return@forEach
            }
        }
    }
}