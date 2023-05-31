package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.LoadedSettings
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.util.TooltipHelper
import org.lwjgl.input.Keyboard

class LunaDebugUIMainPanel() : LunaBaseCustomPanelPlugin()
{



    private var width = 0f
    private var height = 0f

    var currentTabPlugin: LunaDebugUIInterface? = null
    var currentTabPanel: CustomPanelAPI? = null

    companion object
    {
        var selectedTab: String = "Snippets"
        var closeCooldown = 0
    }

    override fun init() {

        enableCloseButton = true

        width = panel!!.position.width
        height = panel!!.position.height

        var element = panel.createUIElement(width, 20f, false)
        var header = element.addSectionHeading("Debug Menu", Alignment.MID, 0f)
        element.position.inTL(0f, 0f)
        panel.addUIElement(element)

        var snippetsButton = LunaUIButton(false, false,width / 3 - -1f, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Snippets"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.position!!.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this!!.position!!.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.backgroundAlpha = 0.5f
            this.borderAlpha = 0.5f

            onSelect {
                selectedTab = "Snippets"
            }

            if (selectedTab == "Snippets")
            {
                setSelected()
            }

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

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }
        }
        snippetsButton.position!!.inTL(0f ,header.position.height)
        element.addTooltipToPrevious(TooltipHelper("Snippets are packets of code that can be executed, which can be added by Modders. Similar to Console Commands.", 300f), TooltipMakerAPI.TooltipLocation.BELOW)

        var entitiesButton = LunaUIButton(false, false,width / 3 -1f, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Entities"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.position!!.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this!!.position!!.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.backgroundAlpha = 0.5f
            this.borderAlpha = 0.5f

            if (selectedTab == "Entities")
            {
                setSelected()
            }

            onSelect {
                selectedTab = "Entities"
            }

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

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }
        }
        entitiesButton.position!!.inTL(width / 3 ,header.position.height)
        element.addTooltipToPrevious(TooltipHelper("A list of all Entities in the Sector.", 300f), TooltipMakerAPI.TooltipLocation.BELOW)

        var itemsButton = LunaUIButton(false, false,width / 3 - 1f, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Cargo"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.position!!.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this!!.position!!.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.backgroundAlpha = 0.5f
            this.borderAlpha = 0.5f

            if (selectedTab == "Cargo")
            {
                setSelected()
            }

            onSelect {
                selectedTab = "Cargo"
            }

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

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }
        }
        itemsButton.position!!.inTL((width / 3) * 2,header.position.height)
        element.addTooltipToPrevious(TooltipHelper("A list of all Items Loaded in to the Game.", 300f), TooltipMakerAPI.TooltipLocation.BELOW)

    }

    override fun advance(amount: Float) {
        if (panel == null) return
        if (currentTabPlugin == null || currentTabPlugin!!.getTab() != selectedTab)
        {
            panel!!.removeComponent(currentTabPanel)
            when (selectedTab)
            {
                "Entities" -> {
                    currentTabPlugin = LunaDebugUIEntitiesPanel()
                    currentTabPanel = panel!!.createCustomPanel(width, height - 60, currentTabPlugin)
                    panel!!.addComponent(currentTabPanel)
                    currentTabPanel!!.position.inTL(0f, 60f)
                    currentTabPlugin!!.init(panel!!, this, currentTabPanel!!)
                }
                "Cargo" -> {
                    currentTabPlugin = LunaDebugUICargoPanel()
                    currentTabPanel = panel!!.createCustomPanel(width, height - 60, currentTabPlugin)
                    panel!!.addComponent(currentTabPanel)
                    currentTabPanel!!.position.inTL(0f, 60f)
                    currentTabPlugin!!.init(panel!!, this, currentTabPanel!!)
                }
                "Snippets" -> {
                    currentTabPlugin = LunaDebugUISnippetsPanel()
                    currentTabPanel = panel!!.createCustomPanel(width, height - 60, currentTabPlugin)
                    panel!!.addComponent(currentTabPanel)
                    currentTabPanel!!.position.inTL(0f, 60f)
                    currentTabPlugin!!.init(panel!!, this, currentTabPanel!!)
                }
            }
        }
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
    }


    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isKeyDownEvent && event.eventValue == LoadedSettings.debugKeybind)
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

                return@forEach
            }
        }
    }
}