package lunalib.backend.ui.settingsV2

import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.settings.LunaSettingsData
import lunalib.backend.ui.settings.LunaSettingsLoader

class LunaSettingsUISettingsPanel() : CustomUIPanelPlugin
{

    var parentPanel: CustomPanelAPI? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null
    var settingsPanels: MutableList<CustomPanelAPI?> = ArrayList()

    var width = 0f
    var height = 0f

    var selectedMod: ModSpecAPI? = null

    fun init(parentPanel: CustomPanelAPI, panel: CustomPanelAPI)
    {
        this.parentPanel = parentPanel
        this.panel = panel

        width = panel.position.width
        height = panel.position.height

        panelElement = panel.createUIElement(width, height, false)
        panelElement!!.position.inTL(0f, 0f)
        panel.addUIElement(panelElement)

    }

    fun recreatePanel()
    {
        if (selectedMod == null) return
        if (subpanel != null)
        {
            settingsPanels.clear()
            panel!!.removeComponent(subpanel)
        }
        subpanel = panel!!.createCustomPanel(width, height, null)
        subpanel!!.position.inTL(0f, 0f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height , true)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(2f)

        //subpanelElement!!.addPara("Test", 0f)

        subpanelElement!!.addSpacer(30f)

        var spacing = 10f
        for (data in LunaSettingsLoader.SettingsData) {
            if (data.modID != selectedMod!!.id) continue

            var headerSpace = 0f
            if (entry == "Test3")
            {
                headerSpace = 20f
                if (spacing >= 11f) {
                    subpanelElement!!.addSpacer(headerSpace)
                    spacing += headerSpace
                }
            }
            var cardPanel = LunaUIPlaceholder(width - 20 , 100f + headerSpace, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            if (entry == "Test3")
            {
                var headerElement = cardPanel.lunaElement!!.createUIElement(width - 20, 100f, false)
                headerElement.position.inTL(0f, 0f)
                cardPanel.uiElement.addComponent(headerElement)
                cardPanel.lunaElement!!.addUIElement(headerElement)
                headerElement.addSectionHeading("Heading", Alignment.MID, 0f)
            }

            var descriptionElement = cardPanel.lunaElement!!.createUIElement(width / 2 - 20, 100f, false)
            descriptionElement.position.inTL(0f, headerSpace)
            cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var name = descriptionElement.addPara("Name here", 0f)
            name.setHighlight("Name here")
            var description = descriptionElement.addPara("This is a description for what should be here if a mod would be loaded, and im running out of things to write here.",0f)

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(width / 2 - 20, 100f, false)
            interactbleElement.position.inTL(10f + width / 2, headerSpace)
            cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            interactbleElement.addSpacer(5f)

            interactbleElement.addPara("Test", 0f)

            spacing += cardPanel.height

            createCard(data, headerSpace)
        }

        var list = listOf("Test3","Test","Test","Test3","Test","Test","Test","Test","Test","Test","Test","Test","Test","Test","Test","Test","Test",)
        for (entry in list)
        {

            if (entry == "Test3")
            {
                headerSpace = 20f
                if (spacing >= 11f) {
                    subpanelElement!!.addSpacer(headerSpace)
                    spacing += headerSpace
                }
            }
            var cardPanel = LunaUIPlaceholder(width - 20 , 100f + headerSpace, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            if (entry == "Test3")
            {
                var headerElement = cardPanel.lunaElement!!.createUIElement(width - 20, 100f, false)
                headerElement.position.inTL(0f, 0f)
                cardPanel.uiElement.addComponent(headerElement)
                cardPanel.lunaElement!!.addUIElement(headerElement)
                headerElement.addSectionHeading("Heading", Alignment.MID, 0f)
            }




            var descriptionElement = cardPanel.lunaElement!!.createUIElement(width / 2 - 20, 100f, false)
            descriptionElement.position.inTL(0f, headerSpace)
            cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var name = descriptionElement.addPara("Name here", 0f)
            name.setHighlight("Name here")
            var description = descriptionElement.addPara("This is a description for what should be here if a mod would be loaded, and im running out of things to write here.",0f)

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(width / 2 - 20, 100f, false)
            interactbleElement.position.inTL(10f + width / 2, headerSpace)
            cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            interactbleElement.addSpacer(5f)

            interactbleElement.addPara("Test", 0f)

            //element.addim

            //settingsElement.position.inTL(0f, spacing)
            /*settingsElement.addPara("Test", 0f)
            settingsElements.add(settingsElement)*/

            //subpanelElement!!.addSpacer(100f)
            //spacing += settingPanTest.height
            spacing += cardPanel.height

        }

        subpanel!!.addUIElement(subpanelElement)
    }

    fun createCard(data: LunaSettingsData, extraSpacing: Float)
    {

    }

    override fun advance(amount: Float) {
        if (LunaSettingsUIModsPanel.selectedMod != null && LunaSettingsUIModsPanel.selectedMod != selectedMod)
        {
            selectedMod = LunaSettingsUIModsPanel.selectedMod
            recreatePanel()
        }
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

       /* val playercolor = Misc.getDarkPlayerColor()

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



    override fun processInput(events: MutableList<InputEventAPI>) {


    }
}