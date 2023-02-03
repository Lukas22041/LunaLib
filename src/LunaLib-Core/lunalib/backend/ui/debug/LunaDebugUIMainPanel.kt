package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.util.TooltipHelper
import org.lwjgl.input.Keyboard

class LunaDebugUIMainPanel() : CustomUIPanelPlugin
{
    var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null

    private var entitiesPanel: CustomPanelAPI? = null
    //private var modsPanelPlugin: LunaSettingsUIModsPanel? = null

    private var itemsPanel: CustomPanelAPI? = null
    //private var settingsPanelPlugin: LunaSettingsUISettingsPanel? = null

    private var width = 0f
    private var height = 0f

    var currentTabPlugin: LunaDebugUIInterface? = null
    var currentTabPanel: CustomPanelAPI? = null

    companion object
    {
        var selectedTab: String = "Snippets"
        var closeCooldown = 0
    }

    fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?, dialog: InteractionDialogAPI?) {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        width = panel!!.position.width
        height = panel!!.position.height

        var element = panel.createUIElement(width, 20f, false)
        var header = element.addSectionHeading("Debug Menu", Alignment.MID, 0f)
        element.position.inTL(0f, 0f)
        panel.addUIElement(element)

        var snippetsButton = LunaUIButton(false, false,width / 3, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Snippets"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height / 2 + this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 4)
            this.backgroundAlpha = 0.5f

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
        element.addTooltipToPrevious(TooltipHelper("A list of code snippets that can be executed that modders can add to. Similar to Console Commands", 300f), TooltipMakerAPI.TooltipLocation.BELOW)

        var entitiesButton = LunaUIButton(false, false,width / 3, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Entities"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height / 2 + this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 4)
            this.backgroundAlpha = 0.5f

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

        var itemsButton = LunaUIButton(false, false,width / 3, 40f, "none", "Tabs", panel!!, element!!).apply {
            this.buttonText!!.text = "Items"
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height / 2 + this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 4)
            this.backgroundAlpha = 0.5f

            if (selectedTab == "Items")
            {
                setSelected()
            }

            onSelect {
                selectedTab = "Items"
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
                "Items" -> {
                    currentTabPlugin = LunaDebugUIItemsPanel()
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

    }

    override fun render(alphaMult: Float) {

    }


    override fun processInput(events: MutableList<InputEventAPI>) {

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isKeyDownEvent && event.eventValue == LoadedSettings.debugKeybind)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                dialog!!.dismiss()

                closeCooldown = 30
                return@forEach
            }
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                dialog!!.dismiss()

                return@forEach
            }
        }
    }
}