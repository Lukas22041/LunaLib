package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.LunaUIColorPicker
import lunalib.backend.ui.components.LunaUIKeybindButton
import lunalib.backend.ui.components.LunaUIRadioButton
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUISprite
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.backend.ui.debug.LunaDebugUISnippetsPanel
import lunalib.backend.util.getLunaString
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
        var lastSelectedMod = "LunaAboutSection"
        var selectedMod: ModSpecAPI? = null
        var lastScroller = 0f
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
            this.buttonText!!.text = "saveButtonName".getLunaString()
            this.buttonText!!.setHighlight("Save All")
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())


            this.uiElement.addTooltipToPrevious(TooltipHelper("saveButtonTooltip".getLunaString(), 300f, "all mods", "changed data", "lost"), TooltipMakerAPI.TooltipLocation.RIGHT)

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
                    this.buttonText!!.setHighlight("Save All")
                }
                else
                {
                    button.buttonText!!.setHighlightColor(Misc.getBasePlayerColor())
                    this.buttonText!!.setHighlight("Save All")
                }

               /* var counter = 0
                for ((key, value) in LunaSettingsUISettingsPanel.unsavedCounter)
                {
                    if (value == 1)
                    {
                        counter++
                    }
                }

                if (counter != 0)
                {
                    button.buttonText!!.text = "saveButtonName".getLunaString() + " ($counter Unsaved)"
                    button.buttonText!!.setHighlight("($counter Unsaved)")
                    button.buttonText!!.setHighlightColors(Misc.getNegativeHighlightColor())
                    this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
                }
                else
                {
                    button.buttonText!!.text = "saveButtonName".getLunaString()
                    this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
                }*/
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
            this.buttonText!!.text = "resetButtonName".getLunaString()
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
            this.uiElement.addTooltipToPrevious(TooltipHelper("resetButtonTooltip".getLunaString(), 300f, "selected mod"), TooltipMakerAPI.TooltipLocation.RIGHT)
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
                if (element is LunaUIRadioButton)
                {
                    var value = (element.key as LunaSettingsData).defaultValue as String
                    element.value = value
                    for (button in element.buttons)
                    {
                        if (button.buttonText!!.text == value)
                        {
                            button.setSelected()
                        }
                    }
                }
            }
            setUnsavedData()
        }

        panelElement!!.addSpacer(3f)

        var aboutButton = LunaUIButton(false, false,width - 15, 30f,"Test", "ModsButton", panel!!, panelElement!!).apply {
            this.buttonText!!.text = "aboutButtonName".getLunaString()
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
            this.buttonText!!.setHighlightColor(Misc.getHighlightColor())

           /* var tooltip1 = "aboutButtonTooltip1".getLunaString()
            var tooltip2 = "aboutButtonTooltip2".getLunaString()
            var tooltip3 = "aboutButtonTooltip3".getLunaString()
            var tooltip4 = "aboutButtonTooltip4".getLunaString()
            var tooltip5 = "aboutButtonTooltip5".getLunaString()

            var tooltip = TooltipHelper(tooltip1 + tooltip2 + tooltip3 + tooltip4 + tooltip5,
                500f, "Lunalib", "not", "hotkey", "new game creation", "persist", "Starsector\\saves\\common\\LunaSettings", "not", "modify", "delete")

            this.uiElement.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.RIGHT)*/
        }
        aboutButton!!.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
      /*  aboutButton!!.onHover {
            backgroundAlpha = 1f
        }
        aboutButton!!.onNotHover {
            backgroundAlpha = 0.5f
        }*/
        aboutButton!!.borderAlpha = 0.5f

        if (lastSelectedMod == "LunaAboutSection")
        {
            selectedMod = null
            aboutButton.setSelected()
        }

        aboutButton.onClick {
            lastSelectedMod = "LunaAboutSection"
            selectedMod = null
            aboutButton.setSelected()
        }

        aboutButton.onUpdate {
            if (isHovering)
            {
                backgroundAlpha = 1f
            }
            else if (isSelected())
            {
                backgroundAlpha = 0.9f
            }
            else
            {
                backgroundAlpha = 0.5f
            }
        }

        panelElement!!.addSpacer(3f)

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
        var para = pan.addPara("searchFieldName".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)
        searchField.borderAlpha = 0.5f
        searchField.run {
            this.uiElement.addTooltipToPrevious(TooltipHelper("searchFieldTooltip".getLunaString(), 300f), TooltipMakerAPI.TooltipLocation.RIGHT)
        }
        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "searchFieldName".getLunaString()
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
            if (element is LunaUIRadioButton)
            {
                changed.add(ChangedSetting(settingsData.modID, settingsData.fieldID, element.value))
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

        // 32 * amount of buttons + their spacers before + an extra gap between mods and the config buttons
        var space = (32f * 4f) + 10f

        subpanel = panel!!.createCustomPanel(width - 9, height - space, null)
        subpanel!!.position.inTL(0f, space)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width - 9, height - space, true)

        subpanelElement!!.position.inTL(0f, 0f)
        //subpanelElement!!.addSpacer(5f)

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()
        val mods: List<ModSpecAPI> = Global.getSettings().modManager.enabledModsCopy.filter { modsWithData.contains( it.id) }

        var spacing = 0f
        for (mod in mods)
        {
            var search = currentSearchText.lowercase()
            var result = FuzzySearch.extractOne(search, listOf(mod.id.lowercase(), mod.name.lowercase()))

            var failed = false

            if (currentSearchText != "" && !mod.name.lowercase().contains(currentSearchText.lowercase()) && !mod.id.lowercase().contains(currentSearchText.lowercase())) failed = true
            if (search != "" && result.score > 60) failed = false
            if (failed) continue


            var cardPanel = LunaUIPlaceholder(true, width - 15 , 60f, mod, "ModsButton", subpanel!!, subpanelElement!!).apply {

                var text = "${mod.name}\n" +
                        "Version: ${mod.version}\n" +
                        "Author: ${mod.author}\n" +
                        "\n" +
                        "${mod.desc}"

                this.uiElement.addTooltipToPrevious(TooltipHelper(text, 400f, "${mod.name}"), TooltipMakerAPI.TooltipLocation.RIGHT)

                this.backgroundAlpha = 0.9f

                if (selectedMod == mod) {
                    this.darkColor = Misc.getDarkPlayerColor().brighter().brighter()
                    this.setSelected()
                }
                else {
                    this.darkColor = Misc.getDarkPlayerColor()
                }
                this.borderAlpha = 0.75f

                onClick {
                    this.setSelected()
                    LunaSettingsUISettingsPanel.lastSelectedTab = ""
                    LunaSettingsUISettingsPanel.lastScroller = 0f
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                }

                onUpdate {
                    if (this.isSelected())
                    {
                        this.darkColor = Misc.getDarkPlayerColor().brighter().brighter()

                    }
                    else
                    {
                        this.darkColor = Misc.getDarkPlayerColor()
                    }
                }

                onHover {
                    borderAlpha = 1f
                }
                onNotHover {
                    borderAlpha = 0.75f
                }

                onHoverEnter {
                    Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
                }

                onSelect {
                    if (!saving) setUnsavedData()
                    selectedMod = mod
                    lastSelectedMod = mod.id
                }
            }

            var spriteElement = cardPanel.lunaElement!!.createUIElement(width - 15, 60f, false)
            spriteElement.position.inTL(0f,0f)

            cardPanel.lunaElement!!.addUIElement(spriteElement)

            var icon = "graphics/icons/default_mod_icon.png"
            var potentialIcon = LunaSettingsConfigLoader.getIconPath(mod.id)
            if (potentialIcon != "") icon = potentialIcon

            Global.getSettings().loadTexture(icon)

            var sprite = LunaUISprite(icon, 40f, 40f, 40f, 40f, 40f, 40f, "", "Group", cardPanel.lunaElement!!, spriteElement!!)
            sprite.position!!.inTL(5f,spriteElement.position!!.height / 2 - sprite.height / 2 )
            sprite.position!!.inTL(5f,spriteElement.position!!.height / 2 - sprite.height / 2 )

            var paragraphElement = cardPanel.lunaElement!!.createUIElement(width - 70, 60f, false)
            paragraphElement.position.inTL(55f,0f)

            cardPanel.lunaElement!!.addUIElement(paragraphElement)

            var text = mod.name
            var para = paragraphElement.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), text)
            para.position.inTL(0f, (30f - para.position.height / 2))

            subpanelElement!!.addSpacer(5f)

            spacing += cardPanel.position!!.height

            if (lastSelectedMod == mod.id)
            {
                cardPanel.setSelected()
                cardPanel.backgroundAlpha = 1f
                selectedMod = mod

            }
            else if (lastSelectedMod == "")
            {
                lastSelectedMod = mod.id
                selectedMod = mod
                cardPanel.setSelected()
            }
        }

        subpanel!!.addUIElement(subpanelElement)
        subpanelElement!!.externalScroller.yOffset = lastScroller
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
                LunaSettingsUISettingsPanel.unsavedCounter.clear()

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

                    if (!newGame) LunaSettings.reportSettingsChanged(mod)

                }


                saving = false
            }
        }

        if (subpanelElement != null)
        {
            if (subpanelElement!!.externalScroller != null)
            {
                lastScroller = subpanelElement!!.externalScroller.yOffset
            }
        }
    }

    override fun processInput(events: MutableList<InputEventAPI>) {


    }

    override fun buttonPressed(buttonId: Any?) {

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