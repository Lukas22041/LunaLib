package lunalib.backend.ui.settingsV2

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipSystemSpecAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.settings.LunaSettingsLoader
import org.lwjgl.opengl.GL11

class LunaSettingsUIModsPanel() : CustomUIPanelPlugin
{

    var parentPanel: CustomPanelAPI? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null
    var searchField: LunaUITextField<String>? = null


    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var saveButton: LunaUIButton? = null
    var resetButton: LunaUIButton? = null

    var width = 0f
    var height = 0f

    var currentSearchText = ""

    companion object
    {
        var selectedMod: ModSpecAPI? = null
    }

    fun init(parentPanel: CustomPanelAPI, panel: CustomPanelAPI)
    {
        this.parentPanel = parentPanel
        this.panel = panel

        width = panel.position.width
        height = panel.position.height

        panelElement = panel.createUIElement(width, height, false)
        panelElement!!.position.inTL(0f, 0f)

        panel.addUIElement(panelElement)

        panelElement!!.addSpacer(3f)

        saveButton = LunaUIButton(false, false,width - 15, 30f,"Test", "SaveButton", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "Save"
            this.buttonText!!.setHighlight("Save")
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())

            onUpdate {
                var button = this as LunaUIButton
                if (selectedMod != null)
                {
                    button.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                }
                else
                {
                    button.buttonText!!.setHighlightColor(Misc.getBasePlayerColor())
                }
            }
        }

        panelElement!!.addSpacer(3f)

        resetButton = LunaUIButton(false, false,width - 15, 30f,"Test", "ResetButton", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "Reset"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
        }

        panelElement!!.addSpacer(4f)

        var searchField = LunaUITextField("",0f, 0f, width - 15, 30f,"Empty", "ModsButtons", panel, panelElement!!).apply {
            onUpdate {
                var field = this as LunaUITextField<String>
                if (field.paragraph != null && isSelected() && currentSearchText != field.paragraph!!.text.replace("_", ""))
                {
                    currentSearchText = field!!.paragraph!!.text.replace("_", "")
                    createModsList()
                }
            }
        }

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
        searchField.uiElement.addComponent(pan)
        searchField.lunaElement!!.addUIElement(pan)
        pan.position.inTL(0f, 0f)
        var para = pan.addPara("Search Mod", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)

        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "Search Mod"
            }
            else
            {
                para.text = ""
            }
        }

        createModsList()


        //panel.removeComponent(subpanel)
    }

    fun createModsList()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }
        subpanel = panel!!.createCustomPanel(width, height - 98, null)
        subpanel!!.position.inTL(0f, 98f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height - 98, true)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(5f)

        var test = mutableListOf<LunaUIBaseElement>()

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()
        val mods: List<ModSpecAPI> = Global.getSettings().modManager.enabledModsCopy.filter { modsWithData.contains( it.id) }

        for (mod in mods)
        {
            if (currentSearchText != "" && !mod.name.lowercase().contains(currentSearchText.lowercase()) && !mod.id.lowercase().contains(currentSearchText.lowercase())) continue
            var button = LunaUIButton(false, false,width - 15, 60f, mod, "ModsButtons", subpanel!!, subpanelElement!!).apply {
                this.buttonText!!.text = "${mod.name.trimAfter(40)}\nVersion: ${mod.version.trimAfter(20)}"
                this.buttonText!!.setHighlight("${mod.name.trimAfter(40)}")
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

                onSelect {
                    selectedMod = mod
                }
            }

            test.add(button)
            subpanelElement!!.addSpacer(5f)
        }

        subpanel!!.addUIElement(subpanelElement)
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {
        /*val playercolor = Misc.getDarkPlayerColor()

        GL11.glPushMatrix()

        GL11.glTranslatef(0f, 0f, 0f)
        GL11.glRotatef(0f, 0f, 0f, 1f)

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glColor4ub(playercolor.red.toByte(), playercolor.green.toByte(), playercolor.blue.toByte(), playercolor.alpha.toByte())

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINE_STRIP)

        GL11.glVertex2f(panel!!.position.x, panel!!.position.y)
        GL11.glVertex2f(panel!!.position.x + panel!!.position.width, panel!!.position.y)
        GL11.glVertex2f(panel!!.position.x + panel!!.position.width, panel!!.position.y + panel!!.position.height)
        GL11.glVertex2f(panel!!.position.x, panel!!.position.y + panel!!.position.height)
        GL11.glVertex2f(panel!!.position.x, panel!!.position.y)

        GL11.glEnd()*/
    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>) {


    }

    private fun String.trimAfter(cap: Int, addText: Boolean = true) : String
    {
        return if (this.length <= cap)
        {
            this
        }
        else
        {
            var text = ""
            if (addText) text = "..."
            this.substring(0, cap).trim() + "..."
        }
    }
}