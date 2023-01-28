package lunalib.backend.ui.settingsV2

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import org.lwjgl.input.Keyboard

class LunaSettingsUIModsPanel() : CustomUIPanelPlugin
{

    var parentPanel: CustomPanelAPI? = null
    var panel: CustomPanelAPI? = null
    var subpanel: CustomPanelAPI? = null

    var element: TooltipMakerAPI? = null

    var width = 0f
    var height = 0f

    fun init(parentPanel: CustomPanelAPI, panel: CustomPanelAPI)
    {
        this.parentPanel = parentPanel
        this.panel = panel

        width = panel.position.width
        height = panel.position.height

        subpanel = panel.createCustomPanel(width, height, null)
        panel.addComponent(subpanel)

        element = subpanel!!.createUIElement(width, height, true)
        //element!!.addPara("TestAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 0f)

        element!!.position.inTL(0f, 0f)
        element!!.addSpacer(5f)

        var test = mutableListOf<LunaUIBaseElement>()

        for (mod in Global.getSettings().modManager.enabledModsCopy)
        {
            var button = LunaUIButton(false, false,width - 15, 60f,"Test", "ModsButtons", subpanel!!, element!!).apply {
                this.buttonText!!.text = "${mod.name}\nVersion: ${mod.version}"
                this.buttonText!!.setHighlight("${mod.name}")
                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                //this.position!!.inTL(0f,0f)
                this.backgroundAlpha = 0.5f

                onUpdate {
                    if (this.isSelected())
                    {
                        this.backgroundAlpha = 1f
                    }
                    else
                    {
                        this.backgroundAlpha = 0.5f
                    }
                }
            }

            test.add(button)
            element!!.addSpacer(5f)
        }

        subpanel!!.addUIElement(element)


        //panel.removeComponent(subpanel)
    }

    fun createModsList()
    {

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


    }
}