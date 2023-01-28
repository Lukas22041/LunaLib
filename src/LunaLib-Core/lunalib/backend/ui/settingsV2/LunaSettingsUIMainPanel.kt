package lunalib.backend.ui.settingsV2

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import org.lwjgl.input.Keyboard

class LunaSettingsUIMainPanel(var newGame: Boolean) : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null

    private var modsPanel: CustomPanelAPI? = null
    private var modsPanelPlugin: LunaSettingsUIModsPanel? = null

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

        modsPanelPlugin = LunaSettingsUIModsPanel()
        modsPanel = panel.createCustomPanel(200f, height * 0.95f, modsPanelPlugin)
        panel.addComponent(modsPanel)
        modsPanel!!.position.inTL(0f, 20f)
        modsPanelPlugin!!.init(panel, modsPanel!!)


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

    override fun processInput(events: MutableList<InputEventAPI>) {

        events.forEach { event ->
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                if (!newGame) dialog!!.dismiss()

                return@forEach
            }
        }

    }
}