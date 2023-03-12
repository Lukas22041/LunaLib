package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarGenDataSpec
import com.fs.starfarer.api.input.InputEventAPI
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
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.lazywizard.lazylib.JSONUtils
import org.lwjgl.input.Keyboard
import java.awt.Color

internal class LunaSettingsUIModsPanel(var newGame: Boolean) : CustomUIPanelPlugin
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

    var saveDelay = 0
    var saving = false

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
            this.buttonText!!.text = "Save all mods"
            this.buttonText!!.setHighlight("Save all mods")
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.uiElement.addTooltipToPrevious(TooltipHelper("Saves the data for all mods that have changed data. If you exit the window without saving, all changes are lost.", 300f), TooltipMakerAPI.TooltipLocation.RIGHT)

            onHover {
                backgroundAlpha = 1f
            }
            onNotHover {
                backgroundAlpha = 0.5f
            }

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }
            onUpdate {
                var button = this as LunaUIButton
                if (LunaSettingsUISettingsPanel.unsaved)
                {
                    button.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                    this.buttonText!!.setHighlight("Save all mods")
                }
                else
                {
                    button.buttonText!!.setHighlightColor(Misc.getBasePlayerColor())
                    this.buttonText!!.setHighlight("Save all mods")
                }
            }
            onClick {
                if (selectedMod == null) return@onClick

                saveDelay = 5
                saving = true

            }
        }
        saveButton!!.borderAlpha = 0.5f


        panelElement!!.addSpacer(3f)

        resetButton = LunaUIButton(false, false,width - 15, 30f,"Test", "SettingGroup", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "Reset to default"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.uiElement.addTooltipToPrevious(TooltipHelper("Resets every option in the currently selected mod (and all of its tabs) back to it's default value. Still needs to be manualy saved.", 300f), TooltipMakerAPI.TooltipLocation.RIGHT)
        }
        resetButton!!.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        resetButton!!.onHover {
            backgroundAlpha = 1f
        }
        resetButton!!.onNotHover {
            backgroundAlpha = 0.5f
        }
        resetButton!!.borderAlpha = 0.5f
        resetButton!!.onClick {
            if (selectedMod == null) return@onClick

            LunaSettingsUISettingsPanel.unsaved = true
            var changed = LunaSettingsUISettingsPanel.changedSettings

            for (data in LunaSettingsLoader.SettingsData)
            {
                if (selectedMod!!.id == data.modID)
                {
                    var c = changed.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
                    if (c != null) changed.remove(c)

                    if (data.fieldType == "Color")
                    {
                        var text = data.defaultValue.toString()
                        if (!text.contains("#"))
                        {
                            text = "#$text"
                        }
                        var color = Color.decode(text.trim().uppercase())
                        changed.add(ChangedSetting(data.modID, data.fieldID, color))
                    }
                    else
                    {
                        changed.add(ChangedSetting(data.modID, data.fieldID, data.defaultValue))
                    }
                }
            }

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
            setUnsavedData()
        }

        panelElement!!.addSpacer(2f)

        var searchField = LunaUITextField("",0f, 0f, width - 15, 30f,"Empty", "Search", panel, panelElement!!).apply {
            onUpdate {
                var field = this as LunaUITextField<String>
                if (field.paragraph != null && isSelected() && currentSearchText != field.paragraph!!.text)
                {
                    currentSearchText = field!!.paragraph!!.text
                    createModsList()
                }
            }
        }

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
       // searchField.uiElement.addComponent(pan)
        searchField.lunaElement!!.addUIElement(pan)
        pan.position.inTL(0f, 0f)
        var para = pan.addPara("Search Mod", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)
        searchField.borderAlpha = 0.5f
        searchField.run {
            this.uiElement.addTooltipToPrevious(TooltipHelper("Select and type in a mods name to filter out all mods that dont match the name or id.", 300f), TooltipMakerAPI.TooltipLocation.RIGHT)

        }
        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
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
            if (isHovering)
            {
                backgroundAlpha = 0.75f
            }
            else if (isSelected())
            {
                backgroundAlpha = 1f
            }
            else
            {
                backgroundAlpha = 0.5f
            }
        }

        createModsList()


        //panel.removeComponent(subpanel)
    }

    fun setUnsavedData()
    {
        var changed = LunaSettingsUISettingsPanel.changedSettings
        var data = LunaSettingsUISettingsPanel.addedElements

        for (element in data)
        {
            var settingsData = element.key as LunaSettingsData

            var c = changed.filter { it.modID == settingsData.modID }.find { it.fieldID == settingsData.fieldID }
            if (c != null) changed.remove(c)

            if (element is LunaUITextField<*>)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.value))
            }
            if (element is LunaUITextFieldWithSlider<*>)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.value))
            }
            if (element is LunaUIColorPicker)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.value))

            }
            if (element is LunaUIButton)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.value))

            }
            if (element is LunaUIKeybindButton)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.keycode))
            }

            //changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, ))
        }
    }

    fun createModsList()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }
        subpanel = panel!!.createCustomPanel(width, height - 96, null)
        subpanel!!.position.inTL(0f, 96f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height - 96, true)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(5f)

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()
        val mods: List<ModSpecAPI> = Global.getSettings().modManager.enabledModsCopy.filter { modsWithData.contains( it.id) }

        for (mod in mods)
        {
            var search = currentSearchText.lowercase()
            var result = FuzzySearch.extractOne(search, listOf(mod.id.lowercase(), mod.name.lowercase()))

            var failed = false

            if (currentSearchText != "" && !mod.name.lowercase().contains(currentSearchText.lowercase()) && !mod.id.lowercase().contains(currentSearchText.lowercase())) failed = true
            if (search != "" && result.score > 50) failed = false
            if (failed) continue

            var button = LunaUIButton(false, false,width - 15, 60f, mod, "ModsButtons", subpanel!!, subpanelElement!!).apply {
                this.buttonText!!.text = "${mod.name.trimAfter(40)}\nVersion: ${mod.version.trimAfter(20)}"
                this.buttonText!!.setHighlight("${mod.name.trimAfter(40)}")
                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                //this.position!!.inTL(0f,0f)


                if (selectedMod == mod) {
                    this.backgroundAlpha = 1f
                    this.setSelected()
                }
                else {
                    this.backgroundAlpha = 0.5f
                }
                this.borderAlpha = 0.5f

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

                onSelect {
                    if (!saving)
                    setUnsavedData()
                    selectedMod = mod
                }

                onClick {
                }
            }

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
        if (saving)
        {
            saveDelay--
            if (saveDelay < 1)
            {
                if (selectedMod == null) return
                setUnsavedData()
                LunaSettingsUISettingsPanel.unsaved = false

                var changed = LunaSettingsUISettingsPanel.changedSettings
                var changedMods = changed.map { it.modID }.distinct()

                for (mod in changedMods)
                {
                    val data = JSONUtils.loadCommonJSON("LunaSettings/${mod}.json", "data/config/LunaSettingsDefault.default");

                    var changedFields = changed.filter { it.modID == mod }

                    for (field in changedFields)
                    {
                        if (field.data is Color)
                        {
                            var color = field.data as Color
                            var hex = String.format("#%02x%02x%02x", color!!.red, color!!.green, color.blue);
                            data.put(field.fieldID, hex)
                        }
                        else
                        {
                            data.put(field.fieldID, field.data)
                        }

                       /* var setting = (element.key as LunaSettingsData)
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
                        }*/
                    }
                    data.save()
                    LunaSettingsLoader.Settings.put(mod, data)

                    if (!newGame) callSettingsChangedListener(selectedMod!!)
                }


                saving = false
            }
        }
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