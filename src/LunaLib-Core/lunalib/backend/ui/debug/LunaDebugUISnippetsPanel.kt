package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.components.LunaUITextFieldWithSlider
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.lunaDebug.*
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.awt.Color

class LunaDebugUISnippetsPanel : LunaDebugUIInterface {

    var parentPanel: CustomPanelAPI? = null
    var parentClass: LunaDebugUIMainPanel? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var outputPanel: CustomPanelAPI? = null

    var snippets: MutableList<LunaSnippet> = ArrayList()

    var width = 0f
    var height = 0f

    // var filters = listOf("Current System", "Starsystems", "Stars", "Planets" ,"Custom Entities", "Fleets")
    companion object
    {
        var lastScroller = 0f

        var searchText = ""

        var activeFilter = "All"

        var filters = mutableListOf("All", LunaSnippet.SnippetTags.Cheat.toString(), LunaSnippet.SnippetTags.Debug.toString(),
            LunaSnippet.SnippetTags.Cargo.toString(),
            LunaSnippet.SnippetTags.Entity.toString(),
            LunaSnippet.SnippetTags.Player.toString(),
            LunaSnippet.SnippetTags.Faction.toString())

        var customFilters: MutableList<String> = ArrayList()
    }

    override fun getTab(): String {
        return "Snippets"
    }

    override fun init(parentPanel: CustomPanelAPI, parentClass: LunaDebugUIMainPanel, panel: CustomPanelAPI) {
        this.parentPanel = parentPanel
        this.panel = panel
        this.parentClass = parentClass

        filters.clear()
        filters = mutableListOf("All", LunaSnippet.SnippetTags.Cheat.toString(), LunaSnippet.SnippetTags.Debug.toString(),
            LunaSnippet.SnippetTags.Cargo.toString(),
            LunaSnippet.SnippetTags.Entity.toString(),
            LunaSnippet.SnippetTags.Player.toString(),
            LunaSnippet.SnippetTags.Faction.toString())
        for (snippet in LunaDebug.snippets)
        {
            for (tag in snippet.tags)
            {
                if (!filters.contains(tag))
                {
                    filters.add(tag)
                }
            }
        }

        width = panel.position.width
        height = panel.position.height

        panelElement = panel.createUIElement(250f, height, false)
        panelElement!!.position.inTL(0f, 0f)

        panel.addUIElement(panelElement)

        var searchField = LunaUITextField("",0f, 0f, 250f, 30f,"Empty", "Debug", panel, panelElement!!).apply {
            onUpdate {

                var field = this as LunaUITextField<String>
                if (field.paragraph != null && isSelected() && searchText != field.paragraph!!.text)
                {
                    searchText = field!!.paragraph!!.text
                    searchForSnippets()
                    createList()
                }
            }
        }
        searchField.paragraph!!.text = searchText
        searchField.value = searchText

        searchField.backgroundAlpha = 0.5f

        searchField.position!!.inTL(25f, 60f)

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
       // searchField.uiElement.addComponent(pan)
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

        var tagsPanel = panel!!.createCustomPanel(260f, height * 0.7f, null)
        tagsPanel!!.position.inTL(20f, 120f)
        panel!!.addComponent(tagsPanel)

        var tagScroller = tagsPanel.createUIElement(260f, height * 0.7f, true)
        tagScroller!!.position.inTL(0f, 0f)
        tagScroller.addSpacer(3f)

        for (filter in filters)
        {
            var button = LunaUIButton(false, false, 250f, 30f, filter, "Debug", panel!!, tagScroller!!).apply {
                var tooltip = when(filter)
                {
                    LunaSnippet.SnippetTags.Cheat.toString() -> "Snippets that generaly allow you to alter values."
                    LunaSnippet.SnippetTags.Debug.toString() -> "Snippets for debugging the game to make finding issues and other kinds of data easier."
                    LunaSnippet.SnippetTags.Cargo.toString() -> "Snippets for interacting with Cargo/Inventory."
                    LunaSnippet.SnippetTags.Entity.toString() -> "Snippets for interacting with Campaign Entities, i.e Fleets, Planets, Stations, etc"
                    LunaSnippet.SnippetTags.Player.toString() -> "Snippets for interactions regarding the player."
                    LunaSnippet.SnippetTags.Faction.toString() -> "Snippets for interacting with a Faction."
                    else -> ""
                }
                if (tooltip != "") tagScroller!!.addTooltipToPrevious(TooltipHelper(tooltip, 300f), TooltipMakerAPI.TooltipLocation.RIGHT)

                this.buttonText!!.text = filter
                this.buttonText!!.position.inTL(this.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)
                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                //this.position!!.inTL(0f,0f)
                this.backgroundAlpha = 0.5f
                this.value = value

                onHoverEnter {
                    Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
                }


                tagScroller!!.addSpacer(5f)
            }



            button.onClick {
                activeFilter = filter

                lastScroller = 0f
                searchForSnippets()
                createList()
            }

            button.onUpdate {
                if (filter == activeFilter)
                {
                    this.backgroundAlpha = 1f
                }
                else
                {
                    this.backgroundAlpha = 0.5f
                }
            }
        }
        tagsPanel.addUIElement(tagScroller)

        searchForSnippets()
        createList()
    }



    fun createList()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }
        if (outputPanel != null)
        {
            panel!!.removeComponent(outputPanel)
        }

        subpanel = panel!!.createCustomPanel(width - 310, height * 0.65f, null)
        subpanel!!.position.inTL(300f, 0f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width - 310, height * 0.65f, true)
        subpanelElement!!.position.inTL(0f, 0f)

        outputPanel = panel!!.createCustomPanel(width - 310, height * 0.30f, null)
        outputPanel!!.position.inTL(300f - 5f, height * 0.66f)
        panel!!.addComponent(outputPanel)
        var outputElement = outputPanel!!.createUIElement(width - 310, height * 0.30f, false)
        outputElement!!.position.inTL(0f, 0f)

        var output = LunaUIPlaceholder(true,width - 310, height * 0.30f, "empty", "none", outputPanel!!, outputElement!!).apply {
            backgroundAlpha = 0.75f
            borderAlpha = 0.75f
        }

        var outputScroller = output.lunaElement!!.createUIElement(output.width, output.height, true)
        outputScroller.position.inTL(0f, 0f)

        outputScroller.addSpacer(3f)
        outputScroller.addPara("Output", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

        output.lunaElement!!.addUIElement(outputScroller)

/*        snippets.addAll(snippets)
        snippets.addAll(snippets)*/
        var spacing = 5f
        for (snippet in snippets)
        {
            var color = Misc.getBasePlayerColor()
            color = Color((color.red * 0.90f).toInt(), (color.green * 0.90f).toInt(), (color.blue * 0.90f).toInt(), 255)

            var requiredSpacing = 0f
            var outputSpacing = 0f
            var cardPanel = LunaUIPlaceholder(true, subpanel!!.position.width - 1 , requiredSpacing, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(0f, spacing)

            var descriptionElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.6f - 20, requiredSpacing, false)
            descriptionElement.position.inTL(0f, 0f)
            //cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var name = "${snippet.getName()}"

            var namePara = descriptionElement.addPara("$name", 0f, Misc.getBrightPlayerColor(), color)
            namePara.setHighlight("")

            descriptionElement.addSpacer(3f)
            var from = "${Global.getSettings().modManager.getModSpec(snippet.getModId()).name} • "
            var categories = "Tags: "
            snippet.tags.forEach { categories += "$it, " }
            categories = categories.substring(0, categories.length - 2)
            var descriptionText = from + categories + "\n\n" + snippet.getDescription()

            var description = descriptionElement.addPara(descriptionText,0f, color, Misc.getHighlightColor())

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

           /* var textWidth = description.computeTextWidth(description.text)
            var textHeight = description.computeTextHeight(description.text)
            var ratio = textWidth / description.position.width
            var extraSpace = textHeight * ratio
            var increase = extraSpace * 0.10f*/

            /*cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + increase)
            subpanelElement!!.addSpacer(increase)*/

            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + description.position.height)
            subpanelElement!!.addSpacer(description.position.height)

            descriptionElement!!.addSpacer(20f)

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.4f - 20, requiredSpacing, false)

            interactbleElement.position.inTL(10f + subpanel!!.position.width * 0.6f, 0f)
            //cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            /*interactbleElement.addSpacer(20f)
            outputSpacing += 20f*/

            var executeButton = LunaUIButton(true, false,250f, 30f, "", "Debug", cardPanel.lunaElement!!, interactbleElement!!)
            executeButton.buttonText!!.text = "Execute"
            executeButton.buttonText!!.position.inTL(executeButton.position!!.width / 2 - executeButton.buttonText!!.computeTextWidth(executeButton.buttonText!!.text) / 2, executeButton!!.position!!.height / 2 - executeButton.buttonText!!.computeTextHeight(executeButton.buttonText!!.text) / 2)
            executeButton.position!!.inTL(interactbleElement.position!!.width / 2 - executeButton.position!!.width / 2, 20f)
            outputSpacing += executeButton.position!!.height

            interactbleElement.addSpacer(20f)
            //outputSpacing += 20f

            var snippetBuilder = SnippetBuilder(cardPanel, cardPanel.lunaElement!!, interactbleElement)
            snippet.addParameters(snippetBuilder)

            outputSpacing += snippetBuilder.totalAddedSpacing
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + outputSpacing)

            var w = width
            var h = height
            var p = panel
            executeButton.onClick {
                var parameters: MutableMap<String, Any> = HashMap()

                outputPanel!!.removeComponent(outputElement)
                p!!.removeComponent(outputPanel)

                outputPanel = panel!!.createCustomPanel(w - 310, h * 0.30f, null)
                outputPanel!!.position.inTL(300f - 5f, h * 0.66f)
                p!!.addComponent(outputPanel)
                var outputElement = outputPanel!!.createUIElement(w - 310, h * 0.30f, false)
                outputElement!!.position.inTL(0f, 0f)

                var output = LunaUIPlaceholder(true,w - 310, h * 0.30f, "empty", "none", outputPanel!!, outputElement!!).apply {
                    backgroundAlpha = 0.75f
                    borderAlpha = 0.75f
                }

                outputScroller = output.lunaElement!!.createUIElement(output.width, output.height, true)
                outputScroller.position.inTL(0f, 0f)

                outputScroller.addSpacer(3f)

                for (element in snippetBuilder.elements)
                {
                    if (element is LunaUITextField<*>)
                    {
                        parameters.put(element.key as String, element.paragraph!!.text!!)
                    }
                    if (element is LunaUITextFieldWithSlider<*>)
                    {
                        parameters.put(element.key as String, element.textField!!.value!!)
                    }
                    if (element is LunaUIButton)
                    {
                        parameters.put(element.key as String, element.value!!)
                    }
                }

                try {
                    snippet.execute(parameters, outputScroller)
                }
                catch (e: Throwable)
                {
                    Global.getLogger(this::class.java).debug("Lunalib: Failed to execute snippet \"${snippet.getName()}\"")
                    outputScroller.addPara("Error: Failed to execute Snippet", 0f)
                }
                output.lunaElement!!.addUIElement(outputScroller)
                outputPanel!!.addUIElement(outputElement)

            }

            spacing += cardPanel.height

            //to create a small gap
            spacing += 5f
            subpanelElement!!.addSpacer(5f)
            subpanelElement!!.addSpacer(outputSpacing)


        }
        subpanelElement!!.addSpacer(20f)
        subpanel!!.addUIElement(subpanelElement)
        subpanelElement!!.externalScroller.yOffset = lastScroller
        outputPanel!!.addUIElement(outputElement)
    }

    fun searchForSnippets()
    {
        snippets.clear()
        var capCount = 0

        for (snippet in LunaDebug.snippets.sortedBy { snippet -> snippet.name})
        {
            try {

                var mod = Global.getSettings().modManager.getModSpec(snippet.getModId())
                var text = searchText.lowercase()

                if (capCount > LoadedSettings.debugEntryCap!!) break

                if (activeFilter != "All" && !snippet.getTags().contains(activeFilter)) continue

                var result = FuzzySearch.extractOne(text, listOf(snippet.name.lowercase(), mod.id.lowercase(), mod.name.lowercase()))
                var failed = false
                if (!snippet.getName().lowercase().contains(text) && !mod.id.lowercase().contains(text) && !mod.name.lowercase().contains(text)) failed = true
                if (result.score > 70) failed = false
                if (failed) continue

                snippets.add(snippet)
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
        if (subpanelElement != null)
        {
            if (subpanelElement!!.externalScroller != null)
            {
                lastScroller = subpanelElement!!.externalScroller.yOffset
            }
        }
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {

    }

    override fun buttonPressed(buttonId: Any?) {

    }
}


