package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.lunaDebug.*
import java.awt.Color

class LunaDebugUISnippetsPanel : LunaDebugUIInterface {

    var parentPanel: CustomPanelAPI? = null
    var parentClass: LunaDebugUIMainPanel? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var snippets: MutableList<LunaSnippet> = ArrayList()

    var width = 0f
    var height = 0f

    // var filters = listOf("Current System", "Starsystems", "Stars", "Planets" ,"Custom Entities", "Fleets")
    companion object
    {
        var searchText = ""
        var filters = mutableMapOf(LunaSnippet.SnippetCategory.Cheat to true, LunaSnippet.SnippetCategory.Debug to true, LunaSnippet.SnippetCategory.Cargo to true, LunaSnippet.SnippetCategory.Entity to true)
    }

    override fun getTab(): String {
        return "Snippets"
    }

    override fun init(parentPanel: CustomPanelAPI, parentClass: LunaDebugUIMainPanel, panel: CustomPanelAPI) {
        this.parentPanel = parentPanel
        this.panel = panel
        this.parentClass = parentClass

        width = panel.position.width
        height = panel.position.height

        panelElement = panel.createUIElement(300f, height, false)
        panelElement!!.position.inTL(0f, 0f)

        panel.addUIElement(panelElement)

        var searchField = LunaUITextField("",0f, 0f, 250f, 30f,"Empty", "Debug", panel, panelElement!!).apply {
            onUpdate {

                var field = this as LunaUITextField<String>
                if (field.paragraph != null && isSelected() && searchText != field.paragraph!!.text.replace("_", ""))
                {
                    searchText = field!!.paragraph!!.text.replace("_", "")
                    searchForSnippets()
                    createList()
                }
            }
        }
        searchField.paragraph!!.text = searchText
        searchField.value = searchText

        searchField.backgroundAlpha = 0.5f

        searchField.position!!.inTL(50f, 60f)

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
        searchField.uiElement.addComponent(pan)
        searchField.lunaElement!!.addUIElement(pan)
        pan.position.inTL(0f, 0f)
        var para = pan.addPara("", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        if (searchText == "")
        {
            para.text = "Search"
        }
        para.position.inTL(searchField!!.position!!.width / 2 - para.computeTextWidth(para.text) / 2 , searchField!!.position!!.height / 2 - para.computeTextHeight(para.text) / 2)

        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "Search"
                para.position.inTL(searchField!!.position!!.width / 2 - para.computeTextWidth(para.text) / 2 , searchField!!.position!!.height / 2 - para.computeTextHeight(para.text) / 2)
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


        panelElement!!.addSpacer(50f)

        for ((key, value) in filters)
        {
            var button = LunaUIButton(false, false,250f, 30f, "none", "Debug", panel!!, panelElement!!).apply {
                this.buttonText!!.text = key.toString()
                this.buttonText!!.position.inTL(this.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                //this.position!!.inTL(0f,0f)
                this.backgroundAlpha = 0.5f
                this.value = value

                onHoverEnter {
                    Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
                }


                panelElement!!.addSpacer(5f)
            }

            button.onClick {
                button.value = !button.value
                filters.put(key, button.value)

                searchForSnippets()
                createList()
            }

            button.onUpdate {
                if (button.value)
                {
                    this.backgroundAlpha = 1f
                }
                else
                {
                    this.backgroundAlpha = 0.5f
                }
            }
        }

        searchForSnippets()
        createList()
    }

    fun createList()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }

        subpanel = panel!!.createCustomPanel(width - 350, height, null)
        subpanel!!.position.inTL(350f, 0f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width - 350, height , true)

        subpanelElement!!.position.inTL(0f, 0f)

        var spacing = 10f
        for (snippet in snippets)
        {
            var color = Misc.getBasePlayerColor()
            color = Color(color.red, color.green, color.blue, 230)

            var requiredSpacing = 110f
            var outputSpacing = 0f
            var cardPanel = LunaUIPlaceholder(true, subpanel!!.position.width - 75, requiredSpacing, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            var descriptionElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.5f - 20, requiredSpacing, false)
            descriptionElement.position.inTL(0f, 0f)
            cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var name = "${snippet.getName()}"

            var namePara = descriptionElement.addPara("$name", 0f, Misc.getBasePlayerColor(), color)
            namePara.setHighlight("")

            descriptionElement.addSpacer(3f)

            var from = "Mod: ${Global.getSettings().modManager.getModSpec(snippet.getModId()).name} | "
            var categories = "Categories: "
            snippet.categories.forEach { categories += "$it, " }
            categories = categories.substring(0, categories.length - 2)
            var descriptionText = from + categories + "\n\n" + snippet.getDescription()

            var description = descriptionElement.addPara(descriptionText,0f, color, color)

            descriptionElement!!.addSpacer(20f)

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.5f - 20, requiredSpacing, false)

            interactbleElement.position.inTL(10f + subpanel!!.position.width * 0.5f, 0f)
            cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            interactbleElement.addSpacer(20f)
            outputSpacing += 50f

            var executeButton = LunaUIButton(true, false,250f, 30f, "", "Debug", cardPanel.lunaElement!!, interactbleElement!!)
            executeButton.buttonText!!.text = "Execute"
            executeButton.buttonText!!.position.inTL(executeButton.position!!.width / 2 - executeButton.buttonText!!.computeTextWidth(executeButton.buttonText!!.text) / 2, executeButton!!.position!!.height / 2 - executeButton.buttonText!!.computeTextHeight(executeButton.buttonText!!.text) / 2)

            outputSpacing += executeButton.position!!.height

            interactbleElement.addSpacer(20f)
            outputSpacing += 20f

            var snippetBuilder = SnippetBuilder(cardPanel, cardPanel.lunaElement!!, interactbleElement)
            snippet.addParameters(snippetBuilder)

            outputSpacing += snippetBuilder.totalAddedSpacing

            var outputElement = cardPanel.lunaElement!!.createUIElement(cardPanel!!.position!!.width - 40, 50f, false)

            outputElement.position.inTL(3f , outputSpacing)
            cardPanel.uiElement.addComponent(outputElement)
            cardPanel.lunaElement!!.addUIElement(outputElement)

            var output = LunaUIPlaceholder(true, descriptionElement.position.width + executeButton.width + 30, outputElement.position.height, "empty", "none", cardPanel.lunaElement!!, outputElement!!).apply {
                backgroundAlpha = 0.25f
                borderAlpha = 0.5f
            }

            var statePara = output.uiElement.addPara("", 0f, color, color)
            statePara.position.inTL(10f, 2f)

            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + output.position!!.height)

            executeButton.onClick {
                var parameters: MutableMap<String, Any> = HashMap()
                for (element in snippetBuilder.elements)
                {
                    if (element is LunaUITextField<*>)
                    {
                        parameters.put(element.key as String, element.value!!)
                    }
                }

                try {
                    statePara.text = snippet.execute(parameters)
                }
                catch (e: Throwable)
                {
                    Global.getLogger(this::class.java).debug("Lunalib: Failed to execute snippet \"${snippet.getName()}\"")
                }


            }

            spacing += cardPanel.height

            //to create a small gap
            spacing += 5f
            subpanelElement!!.addSpacer(5f)

        }
        subpanelElement!!.addSpacer(20f)
        subpanel!!.addUIElement(subpanelElement)
    }

    fun searchForSnippets()
    {
        snippets.clear()
        var capCount = 0



        for (snippet in LunaDebug.snippets)
        {
            try {
                var instance = snippet.newInstance() as LunaSnippet

                var mod = Global.getSettings().modManager.getModSpec(instance.getModId())
                var text = searchText.lowercase()

                if (capCount > LoadedSettings.debugEntryCap!!) break

                var passedFilter = false
                for ((key, value) in filters)
                {
                    if (instance.getCategories().contains(key) && value == true)
                    {
                        passedFilter = true
                        break;
                    }
                }
                if (!passedFilter) continue
                if (!instance.getName().lowercase().contains(text) && !mod.id.lowercase().contains(text) && !mod.name.lowercase().contains(text)) continue
                snippets.add(instance)
            }
            catch (e: Throwable)
            {
                Global.getLogger(this::class.java).debug("Lunalib: Failed to load snippet \"${snippet.name}")
            }
        }
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>?) {

    }
}


