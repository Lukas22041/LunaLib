package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.LunaUIColorPicker
import lunalib.backend.ui.components.LunaUIKeybindButton
import lunalib.backend.ui.components.LunaUIRadioButton
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.*
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.util.getLunaString
import lunalib.lunaExtensions.*
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaUI.elements.LunaSpriteElement
import java.awt.Color
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI

data class ChangedSetting(var modID: String, var fieldID: String, var data: Any?)

internal class LunaSettingsUISettingsPanel() : CustomUIPanelPlugin
{

    enum class SettingsType {
        String, Int, Double, Boolean, Color, Keycode, Radio, Multichoice, Text, Header,
    }

    var parentPanel: CustomPanelAPI? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var tabspanel: CustomPanelAPI? = null
    var tabspanelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var width = 0f
    var height = 0f

    var selectedMod: ModSpecAPI? = null

    var selectedTab = ""
    var currentTabSpacing = 0f


    companion object
    {
        var unsaved = false
        var unsavedCounter: MutableMap<String, Int> = HashMap()
        var changedSettings = ArrayList<ChangedSetting>()
        var addedElements: MutableList<LunaUIBaseElement> = ArrayList()
        var lastSelectedTab = ""
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
    }

    fun recreateTabs()
    {
        if (selectedMod == null) return
        if (tabspanel != null)
        {
            panel!!.removeComponent(tabspanel)
        }

        currentTabSpacing = 0f

        tabspanel = panel!!.createCustomPanel(width, height, null)
        tabspanel!!.position.inTL(0f, 0f)
        panel!!.addComponent(tabspanel)
        tabspanelElement = tabspanel!!.createUIElement(width, height , true)
        tabspanel!!.addUIElement(tabspanelElement)

        tabspanelElement!!.position.inTL(10f, 3f)
        tabspanelElement!!.addSpacer(3f)

        var tabs = LunaSettingsLoader.SettingsData.filter { it.modID == selectedMod!!.id }.map { it.tab }.distinct()

        var rows: MutableMap<Int, MutableList<String>> = HashMap()
        var rowCount = 0

        for (tab in ArrayList(tabs))
        {
            var row = rows.get(rowCount)
            if (row == null)
            {
                rows.put(rowCount, ArrayList())
                row = rows.get(rowCount)
            }

            rows.put(rowCount, row!!)

            if (row!!.size >= 2)
            {
                rowCount++
            }

            row!!.add(tab)
        }

        var test = rows

        var tabHeight = 30f
        var tabX = 0f
        var tabY = 0f
        var first = true

        for (row in rows)
        {
            tabX = 0f
            var tabs = row.value

            for (tab in tabs)
            {
                var gapSpacing = 0f
                if (row.value.size == 2) gapSpacing += 2
                if (row.value.size == 3) gapSpacing += 2

                var name = tab
                if (name == "") name = "General"

                var space = ((tabspanelElement!!.position.width - 15f) / row.value.size) - gapSpacing
                var button = LunaUIButton(false, false, space, tabHeight, "key", "TabsButtons", tabspanel!!, tabspanelElement!!).apply {
                    this.buttonText!!.text = name
                    this.buttonText!!.position.inTL(this.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
                    this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                    //this.position!!.inTL(0f,0f)

                    if (value) this.backgroundAlpha = 0.75f
                    else backgroundAlpha = 0.5f
                    this.borderAlpha = 0.5f

                    onHoverEnter {
                        Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
                    }
                }

                if (lastSelectedTab == "" && first)
                {
                    button.setSelected()
                    selectedTab = tab
                    first = false
                    button.backgroundAlpha = 0.75f
                }
                else if (lastSelectedTab == tab)
                {
                    button.setSelected()
                    lastSelectedTab = tab
                    selectedTab = tab
                    first = false
                    button.backgroundAlpha = 0.75f
                }

                button.position!!.inTL(tabX, tabY)

                tabX += space + gapSpacing * 1.5f
                button.onClick {
                    if (selectedTab != tab)
                    {
                        lastScroller = 0f
                        setUnsavedData()
                        selectedTab = tab
                        lastSelectedTab = tab
                        setSelected()
                        recreatePanel()
                    }
                }

                button.onUpdate {
                    if (isSelected())
                    {
                        if (isHovering)
                        {
                            backgroundAlpha = 0.9f
                        }
                        else
                        {
                            backgroundAlpha = 0.75f
                        }
                    }
                    else
                    {
                        if (isHovering)
                        {
                            backgroundAlpha = 0.6f
                        }
                        else
                        {
                            backgroundAlpha = 0.5f
                        }
                    }
                }

            }

            tabY += tabHeight + 2f

        }
        currentTabSpacing += tabY
    }

    fun recreatePanel()
    {
        addedElements.clear()
        //unsaved = false
        if (selectedMod == null) return
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }
        subpanel = panel!!.createCustomPanel(width, height - currentTabSpacing, null)
        subpanel!!.position.inTL(0f, 0f + currentTabSpacing)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height - currentTabSpacing , true)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(2f)

        subpanelElement!!.addSpacer(100f)

        var spacing = 10f
        for (data in LunaSettingsLoader.SettingsData) {
            if (data.modID != selectedMod!!.id) continue
            if (data.tab != selectedTab) continue

            var requiredSpacing = 30f
            var renderBackground = true

            if (data.fieldType == "Header")
            {
                requiredSpacing = 20f
                renderBackground = false
                if (spacing >= requiredSpacing + 1) {
                    spacing += 20f
                    subpanelElement!!.addSpacer(20f)
                }
            }
            var cardPanel = LunaUIPlaceholder(renderBackground, width - 20 , requiredSpacing, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            if (data.fieldType == SettingsType.Header.toString())
            {
                var headerElement = cardPanel.lunaElement!!.createUIElement(width - 20, requiredSpacing, false)
                headerElement.position.inTL(0f, 0f)

                //cardPanel.uiElement.addComponent(headerElement)
                cardPanel.lunaElement!!.addUIElement(headerElement)
                var header = headerElement.addSectionHeading("${data.defaultValue}", Alignment.MID, 0f)
                spacing += header.position.height

            }
            else if (data.fieldType == SettingsType.Text.toString())
            {
                var descriptionElement = cardPanel.lunaElement!!.createUIElement(width - 20, 10f, false)
                cardPanel!!.position!!.setSize(cardPanel!!.position!!.width, 10f)
                descriptionElement.position.inTL(0f, 0f)
                //cardPanel.uiElement.addComponent(descriptionElement)
                cardPanel.lunaElement!!.addUIElement(descriptionElement)

                var color = Misc.getBasePlayerColor()
                color = Color((color.red * 0.90f).toInt(), (color.green * 0.90f).toInt(), (color.blue * 0.90f).toInt(), 255)

                descriptionElement.addSpacer(5f)
                var description = descriptionElement.addPara("${data.defaultValue}",0f, color, Misc.getHighlightColor())

                var descText = description.text
                var abortAttempts = 100
                var highlights = mutableListOf("")
                while (descText.indexOf("[") != -1 && abortAttempts > 0)
                {
                    abortAttempts--
                    highlights.add(descText.substring(descText.indexOf("[") + 1, descText.indexOf("]")))

                    description.text = description.text.replaceFirst("[", "")
                    description.text = description.text.replaceFirst("]", "")

                    descText = descText.replaceFirst("[", "")
                    descText = descText.replaceFirst("]", "")
                }
                description.setHighlight(*highlights.toTypedArray())

                /*var textWidth = description.computeTextWidth(description.text)
                var textHeight = description.computeTextHeight(description.text)
                var ratio = textWidth / description.position.width
                var extraSpace = textHeight * ratio
                var increase = extraSpace * 1f*/

                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + description.position.height)
                subpanelElement!!.addSpacer(description.position.height)

                spacing += cardPanel.height

                //to create a small gap
                spacing += 5f
                subpanelElement!!.addSpacer(5f)
            }
            else
            {
                var descriptionElement = cardPanel.lunaElement!!.createUIElement(width * 0.6f - 20, requiredSpacing, false)
                descriptionElement.position.inTL(0f, 0f)
                //cardPanel.uiElement.addComponent(descriptionElement)
                cardPanel.lunaElement!!.addUIElement(descriptionElement)

                descriptionElement.addSpacer(5f)

                //descriptionElement.setParaFont("graphics/fonts/futura12.fnt")
               // descriptionElement.setParaSmallInsignia()
                var name = descriptionElement.addPara("${data.fieldName}", 0f, Misc.getBrightPlayerColor(), Misc.getBrightPlayerColor())
                //descriptionElement.setParaFontDefault()

                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + name.position.height)
                subpanelElement!!.addSpacer(name.position.height)

                //name.setHighlight("${data.fieldName}")
                descriptionElement.addSpacer(3f)

                var color = Misc.getBasePlayerColor()
                color = Color((color.red * 0.90f).toInt(), (color.green * 0.90f).toInt(), (color.blue * 0.90f).toInt(), 255)
                var description = descriptionElement.addPara("${data.fieldDescription}",0f, color, Misc.getHighlightColor())

                var descText = description.text
                var abortAttempts = 100
                var highlights = mutableListOf("")
                while (descText.indexOf("[") != -1 && abortAttempts > 0)
                {
                    abortAttempts--
                    highlights.add(descText.substring(descText.indexOf("[") + 1, descText.indexOf("]")))

                    description.text = description.text.replaceFirst("[", "")
                    description.text = description.text.replaceFirst("]", "")

                    descText = descText.replaceFirst("[", "")
                    descText = descText.replaceFirst("]", "")
                }
                description.setHighlight(*highlights.toTypedArray())
                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + description.position.height)
                subpanelElement!!.addSpacer(description.position.height)

                var interactbleElement = cardPanel.lunaElement!!.createUIElement(width * 0.4f - 20, requiredSpacing, false)

                interactbleElement.position.inTL(10f + width * 0.6f, 0f)
                //cardPanel.uiElement.addComponent(interactbleElement)
                cardPanel.lunaElement!!.addUIElement(interactbleElement)

                interactbleElement.addSpacer(5f)

                var createdElement = when (data.fieldType)
                {
                    SettingsType.Int.toString(), SettingsType.Double.toString(),  SettingsType.String.toString() -> createTextFieldCard(data, cardPanel, interactbleElement)
                    SettingsType.Boolean.toString() -> createButtonCard(data, cardPanel, interactbleElement)
                    SettingsType.Color.toString() -> createColorCard(data, cardPanel, interactbleElement)
                    SettingsType.Keycode.toString() -> createKeybindCard(data, cardPanel, interactbleElement)
                    SettingsType.Radio.toString() -> createRadioCard(data, cardPanel, interactbleElement)

                    else -> null
                }



                /*if (createdElement != null)
                {
                    cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + createdElement.position!!.height)
                    subpanelElement!!.addSpacer(description.position.height)
                }*/

                //interactbleElement.addPara("Test", 0f)

                spacing += cardPanel.height

                //to create a small gap
                spacing += 5f
                subpanelElement!!.addSpacer(5f)

            }
        }


        subpanel!!.addUIElement(subpanelElement)
        subpanelElement!!.externalScroller.yOffset = lastScroller

    }

    fun createTextFieldCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI) : LunaUIBaseElement?
    {
        //interactbleElement.addPara("Test", 0f)

        if (data.fieldType == SettingsType.String.toString())
        {
            var height = 30f
            var value = LunaSettings.getString(data.modID, data.fieldID).toString()

            var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
            if (changedData != null) value = changedData.data as String

            var textField = LunaUITextField(value,0f, 0f, 200f, height,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)

          /*  if (cardPanel.position!!.height < height)
            {
                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + textField.position!!.height)
                subpanelElement!!.addSpacer(textField.position!!.height)
            }*/


            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + 2)
            subpanelElement!!.addSpacer(2f)
            textField.onUpdate {
                if (textField.value != LunaSettings.getString(data.modID, data.fieldID).toString())
                {
                    unsaved = true
                    unsavedCounter.put(data.fieldID, 1)
                }
            }

            addedElements.add(textField)
            return textField
        }
        if (data.fieldType == SettingsType.Double.toString())
        {
            var height = 50f
            var value = LunaSettings.getDouble(data.modID, data.fieldID)

            var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
            if (changedData != null) value = changedData.data as Double

            var textField = LunaUITextFieldWithSlider(value, data.minValue.toFloat(),  data.maxValue.toFloat(), 200f, height,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)


            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + 2)
            subpanelElement!!.addSpacer(2f)
            textField.onUpdate {
                if (textField.value != LunaSettings.getDouble(data.modID, data.fieldID))
                {
                    unsaved = true
                    unsavedCounter.put(data.fieldID, 1)
                }
            }

            addedElements.add(textField)
            return textField
        }
        if (data.fieldType == SettingsType.Int.toString())
        {
            var height = 50f
            var value = LunaSettings.getInt(data.modID, data.fieldID)

            var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
            if (changedData != null) value = changedData.data as Int

            var textField = LunaUITextFieldWithSlider(value, data.minValue.toFloat(),  data.maxValue.toFloat(), 200f, height, data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)

            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            textField.onUpdate {
                if (textField.value != LunaSettings.getInt(data.modID, data.fieldID))
                {
                    unsaved = true
                    unsavedCounter.put(data.fieldID, 1)
                }
            }

            addedElements.add(textField)
            return textField
        }
        return null
    }

    fun createButtonCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI) : LunaUIBaseElement?
    {
        var height = 30f
        var value = LunaSettings.getBoolean(data.modID, data.fieldID)

        var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
        if (changedData != null) value = changedData.data as Boolean

        var button = LunaUIButton(value!!, true,200f, height, data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)


        button.position!!.inTL(50f, cardPanel.height / 2 - button.height / 2)
        button.onClick {
            unsaved = true

            if (button.value != LunaSettings.getBoolean(data.modID, data.fieldID))
            {
                unsavedCounter.put(data.fieldID, 1)

            }
            else
            {
                unsavedCounter.put(data.fieldID, 0)
            }
        }

        addedElements.add(button)
        return button
    }

    fun createColorCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI) : LunaUIBaseElement?
    {
        var height = 80f
        var value = LunaSettings.getColor(data.modID, data.fieldID)

        var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
        if (changedData != null) value = changedData.data as Color

        var picker = LunaUIColorPicker(value, false, 200f, height,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)

        if (cardPanel.position!!.height  < height)
        {
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + picker.position!!.height)
            subpanelElement!!.addSpacer(picker.position!!.height)
        }

        picker.position!!.inTL(50f, cardPanel.height / 2 - picker.height / 2)
        picker.onUpdate {
            if (picker.value != LunaSettings.getColor(data.modID, data.fieldID))
            {
                unsaved = true
                unsavedCounter.put(data.fieldID, 1)
            }
        }

        addedElements.add(picker)
        return picker
    }

    fun createKeybindCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI) : LunaUIBaseElement?
    {
        var height = 30f
        var value = LunaSettings.getInt(data.modID, data.fieldID)

        var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
        if (changedData != null) value = changedData.data as Int

        var button = LunaUIKeybindButton(value!!, false, 200f, height,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)

      /*  if (cardPanel.position!!.height < height)
        {
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + button.position!!.height)
            subpanelElement!!.addSpacer(button.position!!.height)
        }*/

        button.position!!.inTL(50f, cardPanel.height / 2 - button.height / 2)
        button.onUpdate {
            if (button.keycode != LunaSettings.getInt(data.modID, data.fieldID))
            {
                unsaved = true
                unsavedCounter.put(data.fieldID, 1)
            }
        }
        addedElements.add(button)


        return button
    }

    fun createRadioCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI) : LunaUIBaseElement?
    {
        var value = LunaSettings.getString(data.modID, data.fieldID)

        var changedData = changedSettings.filter { it.modID == data.modID }.find { it.fieldID == data.fieldID }
        if (changedData != null) value = changedData.data as String

        var choices = (data.secondaryValue as String).split(",").map { it.trim() }

        var radio = LunaUIRadioButton(value!!, choices,200f, 0f, data, data.fieldID, cardPanel.lunaElement!!, interactbleElement)
        radio.position!!.inTL(50f, cardPanel.height / 2 - radio.height / 2)

       /* if (cardPanel.height < 25 * radio.buttons.size)
        {*/
           /* cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + radio.height)
            subpanelElement!!.addSpacer(radio.height)*/
       /* }*/

        cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + 10)
        subpanelElement!!.addSpacer(10f)

        //Add spacing based on the size of the button for each button, until its larger than the total spacing the buttons take up
        for (button in radio.buttons)
        {
            //+5 spacing for the space inbetween each button
            if (cardPanel.height < (25 + 5) * radio.buttons.size)
            {
                //+5 spacing for the space inbetween each button
                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + button.height + 5)
                subpanelElement!!.addSpacer(button.height + 5)
            }
        }
        radio.position!!.inTL(50f, (cardPanel.height / 2 - radio.height / 2) - 25 / 2)

        for (button in radio.buttons)
        {
            button.onClick {
                unsaved = true
                unsavedCounter.put(data.fieldID, 1)
            }
        }

        addedElements.add(radio)
        return radio
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

    override fun advance(amount: Float) {
        if (LunaSettingsUIModsPanel.selectedMod != null && LunaSettingsUIModsPanel.selectedMod != selectedMod)
        {
            createdAbout = false
            selectedMod = LunaSettingsUIModsPanel.selectedMod
            recreateTabs()
            recreatePanel()
        }
        else if (!createdAbout && LunaSettingsUIModsPanel.selectedMod == null && LunaSettingsUIModsPanel.lastSelectedMod == "LunaAboutSection")
        {
            selectedMod = null
            recreateAboutSection()
            createdAbout = true
        }

        if (subpanelElement != null)
        {
            if (subpanelElement!!.externalScroller != null)
            {
                lastScroller = subpanelElement!!.externalScroller.yOffset
            }
        }
    }

    var createdAbout = false
    fun recreateAboutSection()
    {
        if (tabspanel != null)
        {
            panel!!.removeComponent(tabspanel)
        }
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }

        var mHeight = panel!!.position.height * 0.9f

        subpanel = panel!!.createCustomPanel(width, height, null)
        subpanel!!.position.inTL(0f, 3f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height, false)
        subpanel!!.addUIElement(subpanelElement)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(2f)

        subpanelElement!!.addSpacer(100f)

        var cardPanel = LunaUIPlaceholder(true, width - 20 , height - 3, "empty", "none", subpanel!!, subpanelElement!!)
        cardPanel.position!!.inTL(10f, 0f)

        var description = cardPanel.lunaElement!!.createUIElement(width - 20, height- 3, true)
        description.position.inTL(0f, 5f)

        description.setParaFont(Fonts.ORBITRON_24AABOLD)
        var title = description.addPara("aboutTitle".getLunaString(), 0f, Misc.getBrightPlayerColor(), Misc.getHighlightColor())
        description.setParaFont(Fonts.ORBITRON_20AABOLD)
        var subtitle = description.addPara("aboutSubtitle".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        title!!.position.inTL(width / 2 - title!!.computeTextWidth(title!!.text) / 2, 10f)
        subtitle!!.position.inTL(width / 2 - subtitle!!.computeTextWidth(subtitle!!.text) / 2, 35f)


        description.setParaFont(Fonts.DEFAULT_SMALL)
        description.addPara("", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor()).position.inTL(5f, 40f)







        description.addSpacer(20f)
        description.addPara("aboutDescription".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(20f)
        description.addPara("aboutFaQ2".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getPositiveHighlightColor(), "FaQ:")
        description.addSpacer(10f)

        description.addPara("aboutFaQ3".getLunaString(), 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        description.addPara("aboutFaQ4".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(10f)

        description.addPara("aboutFaQ5".getLunaString(), 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        description.addPara("aboutFaQ6".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(10f)

        description.addPara("aboutFaQ7".getLunaString(), 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        description.addPara("aboutFaQ8".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(10f)

        description.addPara("aboutFaQ9".getLunaString(), 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        description.addPara("aboutFaQ10".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(10f)

        description.addPara("aboutFaQ11".getLunaString(), 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        description.addPara("aboutFaQ12".getLunaString(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

       /* for (i in 0..30)
        {
            description.addPara("Test.", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())
        }*/


        description.addSpacer(40f)
        description.addPara("Support", 0f, Misc.getPositiveHighlightColor(), Misc.getPositiveHighlightColor())

        description.addPara("If you encounter an issue with this menu, or require help to integrate it with your own mod, you can message Lukas04#0856 on the Unofficial Starsector Discord. " +
                "Reporting on the Forum thread works aswell, but expect responses to take significantly longer.", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

        description.addSpacer(10f)

        description.addLunaElement(width - 30, 60f).apply {
            renderBackground = false
            renderBorder = false

            var discordLink = "https://discord.com/invite/a8AWVcPCPr"

            var discordButton = innerElement.addLunaElement(200f, 40f)
            discordButton.addText("Open USC Discord", Misc.getBasePlayerColor())
            var tooltipText = "Left click to open in Browser. Rightclick to copy the URL to the clipboard." +
                    "\n\nLink: $discordLink"

            discordButton.addTooltip(tooltipText, 400f, TooltipMakerAPI.TooltipLocation.BELOW, "Warning", "Link")
            discordButton.centerText()
            discordButton.onHoverEnter {
                discordButton.playScrollSound()
                discordButton.borderColor = Misc.getDarkPlayerColor().brighter()
            }
            discordButton.onHoverExit {
                discordButton.borderColor = Misc.getDarkPlayerColor()
            }

            discordButton.onClick {
                discordButton.playClickSound()

                if (it.eventValue == 0)
                {
                    try {
                        Desktop.getDesktop().browse(URI.create(discordLink))
                    } catch (ex: Exception) {

                    }
                }

                if (it.eventValue == 1)
                {
                    val stringSelection = StringSelection(discordLink)
                    val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clipboard.setContents(stringSelection, null)
                }
            }
            discordButton.position.inTL((innerElement!!.position!!.width / 2) - discordButton.width - 30, 20f)

            var forumLink = "https://fractalsoftworks.com/forum/index.php?topic=25658.0"

            var forumButton = innerElement.addLunaElement(200f, 40f)
            forumButton.addText("Open Lunalib Forum", Misc.getBasePlayerColor())
            var tooltipText2 = "Left click to open in Browser. Rightclick to copy the URL to the clipboard." +
                    "\n\nLink: $forumLink"

            forumButton.addTooltip(tooltipText2, 400f, TooltipMakerAPI.TooltipLocation.BELOW, "Warning", "Link")
            forumButton.centerText()
            forumButton.onHoverEnter {
                forumButton.playScrollSound()
                forumButton.borderColor = Misc.getDarkPlayerColor().brighter()
            }
            forumButton.onHoverExit {
                forumButton.borderColor = Misc.getDarkPlayerColor()
            }

            forumButton.onClick {
                forumButton.playClickSound()

                if (it.eventValue == 0)
                {
                    try {
                        Desktop.getDesktop().browse(URI.create(forumLink))
                    } catch (ex: Exception) {

                    }
                }

                if (it.eventValue == 1)
                {
                    val stringSelection = StringSelection(forumLink)
                    val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clipboard.setContents(stringSelection, null)
                }
            }
            forumButton.position.inTL((innerElement!!.position!!.width / 2)  + 10, 20f)


        }

        description.addSpacer(20f)


        cardPanel.lunaElement!!.addUIElement(description)


    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

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