package lunalib.lunaDebug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.debug.LunaDebugUIItemsPanel


class SnippetBuilder(private var cardPanel: LunaUIPlaceholder, private var panel: CustomPanelAPI, private var parameterCard: TooltipMakerAPI) {

    var elements: MutableList<LunaUIBaseElement> = ArrayList()

    fun addStringParameter(name: String, key: String) {
        var textField = LunaUITextField("",0f, 0f, 250f, 30f,key, "Debug", panel, parameterCard!!)
        textField.paragraph!!.text = ""
        textField.value = ""

        var pan = textField.lunaElement!!.createUIElement(textField.position!!.width, textField.position!!.height, false)
        textField.uiElement.addComponent(pan)
        textField.lunaElement!!.addUIElement(pan)
        textField.backgroundAlpha = 0.25f

        pan.position.inTL(0f, 0f)
        var para = pan.addPara(name, 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)

        textField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        textField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = name
                para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)
            }
            else
            {
                para.text = ""
            }
            if (isHovering)
            {
                backgroundAlpha = 0.5f
            }
            else if (isSelected())
            {
                backgroundAlpha = 0.75f
            }
            else
            {
                backgroundAlpha = 0.25f
            }
        }
        parameterCard.addSpacer(5f)
        cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + textField.position!!.height)
        elements.add(textField)
    }
}

enum class SnippetCategory {
    Cheat, Debug, Item, Entity
}

interface LunaSnippet {
    fun getName() : String
    fun getDescription() : String

    /** Id of the mod adding this Snippet*/
    fun getModId() : String
    fun getCategory() : SnippetCategory

    fun addParameters(builder: SnippetBuilder)

    fun execute(parameters: Map<String, Any>) : String

}

