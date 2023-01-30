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
import lunalib.backend.ui.components.LunaUIColorPicker
import lunalib.backend.ui.components.LunaUIKeybindButton
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.settings.LunaSettingsData
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.settings.LunaSettingsUI
import lunalib.lunaSettings.LunaSettingsListener
import org.lazywizard.lazylib.JSONUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class LunaSettingsUIModsPanel(var newGame: Boolean) : CustomUIPanelPlugin
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

    private fun callSettingsChangedListener(data: ModSpecAPI)
    {
        val listeners = Global.getSector().listenerManager.getListeners(LunaSettingsListener::class.java)
        for (listener in listeners)
        {
            try {
                listener.settingsChanged(data.id)
            }
            catch (e: Throwable) {
                Global.getLogger(this.javaClass).debug("Failed to call LunaSettings listener for ${data.id}")
            }
        }
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

        saveButton = LunaUIButton(false, false,width - 15, 30f,"Test", "SettingGroup", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "Save current mod"
            this.buttonText!!.setHighlight("Save current mod")
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())

            onUpdate {
                var button = this as LunaUIButton
                if (LunaSettingsUISettingsPanel.unsaved)
                {
                    button.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                    this.buttonText!!.setHighlight("Save current mod")
                }
                else
                {
                    button.buttonText!!.setHighlightColor(Misc.getBasePlayerColor())
                    this.buttonText!!.setHighlight("Save current mod")
                }
            }
            onClick {
                if (selectedMod == null) return@onClick
                LunaSettingsUISettingsPanel.unsaved = false
                val data = JSONUtils.loadCommonJSON("LunaSettings/${selectedMod!!.id}.json", "data/config/LunaSettingsDefault.default");
                for (element in LunaSettingsUISettingsPanel.addedElements)
                {
                    var setting = (element.key as LunaSettingsData)
                    if (element is LunaUITextField<*>)
                    {
                        data.put(setting.fieldID, element.value)
                    }
                    if (element is LunaUITextFieldWithSlider<*>)
                    {
                        data.put(setting.fieldID, element.value)
                    }
                    if (element is LunaUIColorPicker)
                    {
                        var color = element.value
                        var hex = String.format("#%02x%02x%02x", color!!.red, color!!.green, color.blue);
                        data.put(setting.fieldID, hex)
                    }
                    if (element is LunaUIButton)
                    {
                        data.put(setting.fieldID, element.value)
                    }
                    if (element is LunaUIKeybindButton)
                    {
                        data.put(setting.fieldID, element.keycode)
                    }
                }
                data.save()
                LunaSettingsLoader.Settings.put(selectedMod!!.id, data)

                if (!newGame) callSettingsChangedListener(selectedMod!!)
            }
        }

        panelElement!!.addSpacer(3f)

        resetButton = LunaUIButton(false, false,width - 15, 30f,"Test", "SettingGroup", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "Reset to default"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
        }
        resetButton!!.onClick {
            if (selectedMod == null) return@onClick
            for (element in LunaSettingsUISettingsPanel.addedElements)
            {
                if (element is LunaUITextField<*>)
                {
                    element.updateValue((element.key as LunaSettingsData).defaultValue)
                }
                if (element is LunaUITextFieldWithSlider<*>)
                {
                    element.updateValue((element.key as LunaSettingsData).defaultValue)
                }
                if (element is LunaUIColorPicker)
                {
                    try {
                        var text = (element.key as LunaSettingsData).defaultValue as String
                        if (!text.contains("#"))
                        {
                            text = "#$text"
                        }
                        var color = Color.decode(text.trim().uppercase())
                        element.updateValue(color, text)
                    } catch (e: Throwable) {}
                }
                if (element is LunaUIButton)
                {
                    element.value = (element.key as LunaSettingsData).defaultValue as Boolean
                }
                if (element is LunaUIKeybindButton)
                {
                    var value = (element.key as LunaSettingsData).defaultValue as Int
                    element.keycode = value

                    if (value == 0)
                    {
                        element.button!!.buttonText!!.text = "Key: None"
                        element.button!!.buttonText!!.setHighlight("None")
                    }
                    else
                    {
                        element.button!!.buttonText!!.text = "Key: ${Keyboard.getKeyName(value!!)}"
                        element.button!!.buttonText!!.setHighlight("${Keyboard.getKeyName(value!!)}")
                    }
                    element.button!!.buttonText!!.position.inTL(element.width / 2 - element.button!!.buttonText!!.computeTextWidth(element.button!!.buttonText!!.text) / 2, element.height / 2 - element.button!!.buttonText!!.computeTextHeight(element.button!!.buttonText!!.text) / 2)

                }
            }
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