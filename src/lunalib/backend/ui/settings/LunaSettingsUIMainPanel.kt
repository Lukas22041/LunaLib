package lunalib.backend.ui.settings

import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.backend.scripts.CombatHandler
import lunalib.backend.scripts.LoadedSettings
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.util.TooltipHelper
import org.lwjgl.input.Keyboard
import lunalib.backend.util.*

//I dont recommend anyone to read through my UI code to learn from, its equivelant to the ramblings of an insane person, and such can only be understood by the crazy person themself.
class LunaSettingsUIMainPanel(var newGame: Boolean) : LunaBaseCustomPanelPlugin()
{

    var handler: CombatHandler? = null

    private var modsPanel: CustomPanelAPI? = null
    private var modsPanelPlugin: LunaSettingsUIModsPanel? = null

    private var settingsPanel: CustomPanelAPI? = null
    private var settingsPanelPlugin: LunaSettingsUISettingsPanel? = null

    private var width = 0f
    private var height = 0f

    companion object
    {
        var panelOpen = false
        var closeCooldown = 0
    }


    override fun init() {

        enableCloseButton = true
        panelOpen = true

        width = panel!!.position.width
        height = panel!!.position.height

        var element = panel.createUIElement(width, 20f, false)
        var header = element.addSectionHeading("header".getLunaString(), Alignment.MID, 0f)
        element.addTooltipToPrevious(TooltipHelper("headerTooltip".getLunaString()
            , 500f), TooltipMakerAPI.TooltipLocation.BELOW)

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
        super.renderBelow(alphaMult)


       /* var color = Color(0, 0, 10)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * (alphaMult * 1f))

        GL11.glRectf(panel!!.position.x, panel!!.position.y , panel!!.position.x + width, panel!!.position.y + height)

        GL11.glEnd()
        GL11.glPopMatrix()*/

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
    }

    override fun advance(amount: Float) {

    }

    override fun onClose() {
        super.onClose()

        LunaSettingsUIModsPanel.selectedMod = null
        LunaSettingsUISettingsPanel.addedElements.clear()
        LunaSettingsUISettingsPanel.changedSettings.clear()
        LunaSettingsUISettingsPanel.unsavedCounter.clear()
        LunaSettingsUISettingsPanel.unsaved = false

        //Not clearing this will cause a memory leak
        LunaUIBaseElement.selectedMap.clear()

        panelOpen = false

        if (handler != null)
        {
            handler!!.closeSettingsUI()
        }

    }

    override fun processInput(events: MutableList<InputEventAPI>) {

        super.processInput(events)

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isConsumed) return@forEach
            if (isOpenedFromScript()) return@forEach
            if (event.isKeyDownEvent && event.eventValue == LoadedSettings.settingsKeybind)
            {
                event.consume()

                close()



                closeCooldown = 30
                return@forEach
            }
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                close()

                panelOpen = false

                //Not clearing this will cause a memory leak
                LunaUIBaseElement.selectedMap.clear()

                return@forEach
            }
        }
    }
}