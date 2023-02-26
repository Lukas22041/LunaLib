package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.LunaUIColorPicker
import lunalib.backend.ui.components.LunaUIKeybindButton
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.lunaSettings.LunaSettings
import java.awt.Color

internal class LunaSettingsUISettingsPanel() : CustomUIPanelPlugin
{

    enum class SettingsType {
        String, Int, Double, Boolean, Color, Keycode, Text, Header, //Enum
    }

    var parentPanel: CustomPanelAPI? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var width = 0f
    var height = 0f

    var selectedMod: ModSpecAPI? = null

    companion object
    {
        var unsaved = false
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

    fun recreatePanel()
    {
        addedElements.clear()
        unsaved = false
        if (selectedMod == null) return
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }
        subpanel = panel!!.createCustomPanel(width, height, null)
        subpanel!!.position.inTL(0f, 0f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height , true)

        subpanelElement!!.position.inTL(0f, 0f)
        subpanelElement!!.addSpacer(2f)

        subpanelElement!!.addSpacer(100f)

        var spacing = 10f
        for (data in LunaSettingsLoader.SettingsData) {
            if (data.modID != selectedMod!!.id) continue

            var requiredSpacing = 50f
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
                cardPanel.uiElement.addComponent(headerElement)
                cardPanel.lunaElement!!.addUIElement(headerElement)
                var header = headerElement.addSectionHeading("${data.defaultValue}", Alignment.MID, 0f)
                spacing += header.position.height

            }
            else if (data.fieldType == SettingsType.Text.toString())
            {
                var descriptionElement = cardPanel.lunaElement!!.createUIElement(width - 20, requiredSpacing / 2, false)
                descriptionElement.position.inTL(0f, 0f)
                cardPanel.uiElement.addComponent(descriptionElement)
                cardPanel.lunaElement!!.addUIElement(descriptionElement)

                descriptionElement.addSpacer(5f)
                var description = descriptionElement.addPara("${data.defaultValue}",0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

                var textWidth = description.computeTextWidth(description.text)
                var textHeight = description.computeTextHeight(description.text)
                var ratio = textWidth / description.position.width
                var extraSpace = textHeight * ratio
                var increase = extraSpace

                cardPanel!!.position!!.setSize(cardPanel!!.position!!.width, cardPanel!!.position!!.height  + increase)
                subpanelElement!!.addSpacer(increase)

                spacing += cardPanel.height

                //to create a small gap
                spacing += 5f
                subpanelElement!!.addSpacer(5f)
            }
            else
            {
                var descriptionElement = cardPanel.lunaElement!!.createUIElement(width * 0.6f - 20, requiredSpacing, false)
                descriptionElement.position.inTL(0f, 0f)
                cardPanel.uiElement.addComponent(descriptionElement)
                cardPanel.lunaElement!!.addUIElement(descriptionElement)

                descriptionElement.addSpacer(5f)

                var name = descriptionElement.addPara("${data.fieldName}", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
                //name.setHighlight("${data.fieldName}")
                descriptionElement.addSpacer(3f)

                var color = Misc.getBasePlayerColor()
                color = Color(color.red, color.green, color.blue, 230)
                var description = descriptionElement.addPara("${data.fieldDescription}",0f, color, color)

                var textWidth = description.computeTextWidth(description.text)
                var textHeight = description.computeTextHeight(description.text)
                var ratio = textWidth / description.position.width
                var extraSpace = textHeight * ratio
                var increase = extraSpace

                cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + increase)
                subpanelElement!!.addSpacer(increase)

                var interactbleElement = cardPanel.lunaElement!!.createUIElement(width * 0.4f - 20, requiredSpacing, false)

                interactbleElement.position.inTL(10f + width * 0.6f, 0f)
                cardPanel.uiElement.addComponent(interactbleElement)
                cardPanel.lunaElement!!.addUIElement(interactbleElement)

                interactbleElement.addSpacer(5f)

                when (data.fieldType)
                {
                    SettingsType.Int.toString(), SettingsType.Double.toString(),  SettingsType.String.toString() -> createTextFieldCard(data, cardPanel, interactbleElement)
                    SettingsType.Boolean.toString() -> createButtonCard(data, cardPanel, interactbleElement)
                    SettingsType.Color.toString() -> createColorCard(data, cardPanel, interactbleElement)
                    SettingsType.Keycode.toString() -> createKeybindCard(data, cardPanel, interactbleElement)
                }

                //interactbleElement.addPara("Test", 0f)

                spacing += cardPanel.height

                //to create a small gap
                spacing += 5f
                subpanelElement!!.addSpacer(5f)

            }
        }


        subpanel!!.addUIElement(subpanelElement)
    }

    fun createTextFieldCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI)
    {
        //interactbleElement.addPara("Test", 0f)

        if (data.fieldType == SettingsType.String.toString())
        {
            var value = LunaSettings.getString(data.modID, data.fieldID).toString()
            var textField = LunaUITextField(value,0f, 0f, 200f, 30f,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height / 2)
            textField.onUpdate {
                if (textField.value != LunaSettings.getString(data.modID, data.fieldID).toString())
                {
                    unsaved = true
                }
            }
            addedElements.add(textField)
        }
        if (data.fieldType == SettingsType.Double.toString())
        {
            var value = LunaSettings.getDouble(data.modID, data.fieldID)
            var textField = LunaUITextFieldWithSlider(value, data.minValue.toFloat(),  data.maxValue.toFloat(), 200f, 30f,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height * 0.75f)
            textField.onUpdate {
                if (textField.value != LunaSettings.getDouble(data.modID, data.fieldID))
                {
                    unsaved = true
                }
            }
            addedElements.add(textField)
        }
        if (data.fieldType == SettingsType.Int.toString())
        {
            var value = LunaSettings.getInt(data.modID, data.fieldID)
            var textField = LunaUITextFieldWithSlider(value, data.minValue.toFloat(),  data.maxValue.toFloat(), 200f, 30f, data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
            textField.position!!.inTL(50f, cardPanel.height / 2 - textField.height * 0.75f)
            textField.onUpdate {
                if (textField.value != LunaSettings.getInt(data.modID, data.fieldID))
                {
                    unsaved = true
                }
            }
            addedElements.add(textField)
        }
    }

    fun createButtonCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI)
    {
        var value = LunaSettings.getBoolean(data.modID, data.fieldID)
        var button = LunaUIButton(value!!, true,200f, 30f, data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
        button.position!!.inTL(50f, cardPanel.height / 2 - button.height / 2)
        button.onClick {
            unsaved = true
        }
        addedElements.add(button)
    }

    fun createColorCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI)
    {
        cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + 50f)
        subpanelElement!!.addSpacer(50f)

        var value = LunaSettings.getColor(data.modID, data.fieldID)
        var picker = LunaUIColorPicker(value, false, 200f, 80f,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
        picker.position!!.inTL(50f, cardPanel.height / 2 - picker.height / 2)
        picker.onUpdate {
            if (picker.value != LunaSettings.getColor(data.modID, data.fieldID))
            {
                unsaved = true
            }
        }
        addedElements.add(picker)
    }

    fun createKeybindCard(data: LunaSettingsData, cardPanel: LunaUIPlaceholder,  interactbleElement: TooltipMakerAPI)
    {
        var value = LunaSettings.getInt(data.modID, data.fieldID)
        var button = LunaUIKeybindButton(value!!, false, 200f, 30f,data, "SettingGroup", cardPanel.lunaElement!!, interactbleElement)
        button.position!!.inTL(50f, cardPanel.height / 2 - button.height / 2)
        button.onUpdate {
            if (button.keycode != LunaSettings.getInt(data.modID, data.fieldID))
            {
                unsaved = true
            }
        }
        addedElements.add(button)
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