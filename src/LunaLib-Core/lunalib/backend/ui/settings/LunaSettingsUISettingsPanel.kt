package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.VisualPanelAPI
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.LunaUIColorPicker
import lunalib.backend.ui.components.LunaUIKeybindButton
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.*
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.lunaExtensions.addTransientScript
import lunalib.lunaSettings.LunaSettings
import java.awt.Color

data class ChangedSetting(var modID: String, var fieldID: String, var data: Any?)

internal class LunaSettingsUISettingsPanel() : CustomUIPanelPlugin
{

    enum class SettingsType {
        String, Int, Double, Boolean, Color, Keycode, Text, Header, //Enum
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
        var changedSettings = ArrayList<ChangedSetting>()
        var addedElements: MutableList<LunaUIBaseElement> = ArrayList()
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

                if (first)
                {
                    button.setSelected()
                    selectedTab = tab
                    first = false
                    button.backgroundAlpha = 0.75f
                }

                button.position!!.inTL(tabX, tabY)

                tabX += space + gapSpacing * 1.5f
                button.onClick {
                    if (selectedTab != tab)
                    {
                        setUnsavedData()
                        selectedTab = tab
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

            /*if (cardPanel.position!!.height < height)
            {
                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + textField.position!!.height)
                subpanelElement!!.addSpacer(textField.position!!.height)

            }
*/

            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + 2)
            subpanelElement!!.addSpacer(2f)
            textField.onUpdate {
                if (textField.value != LunaSettings.getDouble(data.modID, data.fieldID))
                {
                    unsaved = true
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

           /* if (cardPanel.position!!.height < height)
            {
                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + textField.position!!.height)
                subpanelElement!!.addSpacer(textField.position!!.height)
            }*/
            /*cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + 12)
            subpanelElement!!.addSpacer(12f)*/

            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            textField.onUpdate {
                if (textField.value != LunaSettings.getInt(data.modID, data.fieldID))
                {
                    unsaved = true
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

        /*var com = Global.getSettings().getCommoditySpec("supplies")

        var test = LunaUISprite(com.iconName, 80f, 80f, data, "Group", cardPanel.lunaElement!!, interactbleElement)*/


      /*  if (cardPanel.position!!.height < height)
        {
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + button.position!!.height)
            subpanelElement!!.addSpacer(button.position!!.height)
        }*/

        button.position!!.inTL(50f, cardPanel.height / 2 - button.height / 2)
        button.onClick {
            unsaved = true
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
            }
        }
        addedElements.add(button)
        return button
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

    override fun advance(amount: Float) {
        if (LunaSettingsUIModsPanel.selectedMod != null && LunaSettingsUIModsPanel.selectedMod != selectedMod)
        {
            selectedMod = LunaSettingsUIModsPanel.selectedMod
            recreateTabs()
            recreatePanel()
        }

    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

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