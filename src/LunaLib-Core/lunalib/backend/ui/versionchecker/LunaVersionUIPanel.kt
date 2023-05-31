package lunalib.backend.ui.versionchecker

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.CombatHandler
import lunalib.backend.ui.components.base.*
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.backend.ui.debug.LunaDebugUISnippetsPanel
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.versionchecker.UpdateInfo.ModInfo
import lunalib.backend.util.getLunaString
import lunalib.lunaExtensions.*
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.util.concurrent.Future


//I dont recommend anyone to read through my UI code to learn from, its equivelant to the ramblings of an insane person, and such can only be understood by the crazy person themself.
class LunaVersionUIPanel() : LunaBaseCustomPanelPlugin()
{

    var handler: CombatHandler? = null

    var leftPanel: CustomPanelAPI? = null
    var leftElement: TooltipMakerAPI? = null

    var modsPanel: CustomPanelAPI? = null
    var modsElement: TooltipMakerAPI? = null

    var rightPanel: CustomPanelAPI? = null
    var rightElement: TooltipMakerAPI? = null

    private var width = 0f
    private var height = 0f

    private val indexThread = "http://fractalsoftworks.com/forum/index.php?topic=177.0"
    private val discordLink = "https://discord.com/invite/a8AWVcPCPr"

    var currentSearchText = ""

    companion object {
        var closeCooldown = 0

        var lastScroller = 0f

        @JvmStatic
        var futureUpdateInfo: Future<UpdateInfo>? = null
        @JvmStatic
        var updateInfo: UpdateInfo? = null
        @JvmStatic
        var unsupportedMods: MutableList<ModSpecAPI> = ArrayList()
        var reconstruct = false
        var panelOpen = false

        var selectedMod: ModInfo? = null

        @JvmStatic
        fun getUpdatedModsCount() : Int
        {
            if (updateInfo == null) return 0
            else return updateInfo!!.hasUpdate.size
        }
    }

    override fun init() {

        enableCloseButton = true
        panelOpen = true

        width = panel!!.position.width
        height = panel!!.position.height

        var element = panel.createUIElement(width, 20f, false)
        var header = element.addSectionHeading("Version Checker", Alignment.MID, 0f)
        element.addTooltipToPrevious(TooltipHelper("headerTooltip".getLunaString()
            , 500f), TooltipMakerAPI.TooltipLocation.BELOW)

        element.position.inTL(0f, 0f)
        panel.addUIElement(element)

       /* pW = this.panel!!.position.width
        pH = this.panel!!.position.height

        reset()*/

        constructLeftPanel()
    }

    fun constructLeftPanel()
    {
        if (leftPanel != null)
        {
            panel!!.removeComponent(leftPanel)
        }

        leftPanel = panel!!.createCustomPanel(250f, height * 0.96f, null)
        leftPanel!!.position.inTL(0f, 20f)
        panel!!.addComponent(leftPanel)
        leftElement = leftPanel!!.createUIElement(250f, height * 0.96f, false)
        leftPanel!!.addUIElement(leftElement)

        leftElement!!.position.inTL(0f, 0f)

        if (updateInfo == null)
        {
            leftElement!!.addPara("No Update Data available, an error may have occured.", 0f)
            return
        }

        leftElement!!.addSpacer(3f)

        var starsectorButton = LunaUIButton(false, false,250f - 15, 30f,"", "Button", leftPanel!!, leftElement!!).apply {

            this.buttonText!!.text = "Starsector Version"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)


            var text = ""
            if (updateInfo!!.isStarsectorAhead)
            {
                this.buttonText!!.setHighlight("Starsector Version")
                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())

                text = "New Starsector Version Available!\n" +
                        "\n" +
                        "Installed Version: ${Global.getSettings().getVersionString()}\n" +
                        "Latest Version: ${updateInfo!!.ssUpdate}"
            }
            else
            {
                text = "The installed Starsector is up to date.\n" +
                        "\n" +
                        "Installed Version: ${Global.getSettings().getVersionString()}\n" +
                        "Latest Version: ${updateInfo!!.ssUpdate}"
            }

            this.uiElement.addTooltipToPrevious(TooltipHelper(text, 400f, "Installed Version", "Latest Version"), TooltipMakerAPI.TooltipLocation.RIGHT)

            onHover {
                backgroundAlpha = 1f
            }
            onNotHover {
                backgroundAlpha = 0.5f
            }

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }
        }
        starsectorButton.borderAlpha = 0.5f

        leftElement!!.addSpacer(3f)

        var indexButton = LunaUIButton(false, false,250f - 15, 30f,"", "Button", leftPanel!!, leftElement!!).apply {
            this.buttonText!!.text = "Mod Index"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)

            this.uiElement.addTooltipToPrevious(TooltipHelper("The mod index is a list of many of the available mods on the Starsector Forum. \n\n" +
                    "Left click to open in Browser. Rightclick to copy the URL to the clipboard.", 400f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)

            onHover {
                backgroundAlpha = 1f
            }
            onNotHover {
                backgroundAlpha = 0.5f
            }

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }

            onClick {

                if (it.eventValue == 0)
                {
                    try {
                        Desktop.getDesktop().browse(URI.create(indexThread))
                    } catch (ex: Exception) {

                    }
                }

                if (it.eventValue == 1)
                {
                    val stringSelection = StringSelection(indexThread)
                    val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clipboard.setContents(stringSelection, null)
                }
            }
        }
        indexButton.borderAlpha = 0.5f
        //subpanelElement!!.addPara("Test", 0f)

        leftElement!!.addSpacer(3f)

        var uscButton = LunaUIButton(false, false,250f - 15, 30f,"", "Button", leftPanel!!, leftElement!!).apply {
            this.buttonText!!.text = "Unofficial Discord"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)

            this.uiElement.addTooltipToPrevious(TooltipHelper("The Unofficial Starsector Discord server has a #mod_updates channel in which many modders will release beta releases for their mods before they create a forum page." +
                    "\n\nLeft click to open in Browser. Rightclick to copy the URL to the clipboard.", 400f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)

            onHover {
                backgroundAlpha = 1f
            }
            onNotHover {
                backgroundAlpha = 0.5f
            }

            onHoverEnter {
                Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
            }

            onClick {

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
        }
        uscButton.borderAlpha = 0.5f
        //subpanelElement!!.addPara("Test", 0f)

        leftElement!!.addSpacer(3f)

        var searchField = LunaUITextField("",0f, 0f, 250f - 15, 30f,"Empty", "Search", leftPanel!!, leftElement!!).apply {
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
        var para = pan.addPara("Search", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)
        searchField.borderAlpha = 0.5f
        searchField.run {
            this.uiElement.addTooltipToPrevious(TooltipHelper("Search for a specific mod", 300f), TooltipMakerAPI.TooltipLocation.RIGHT)
        }
        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "Search"
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

        leftElement!!.addSpacer(3f)



        createModsList()

    }

    fun createModsList()
    {
        if (modsPanel != null)
        {
            panel!!.removeComponent(modsPanel)
        }

        // 32 * amount of buttons + their spacers before + an extra gap between mods and the config buttons
        var space = ((30f * 5f) + 5f)

        modsPanel = panel!!.createCustomPanel(250f - 9 , height - space, null)
        modsPanel!!.position.inTL(0f, space)
        panel!!.addComponent(modsPanel)
        modsElement = modsPanel!!.createUIElement(250f - 9 , height - space, true)

        modsElement!!.position.inTL(0f, 0f)
        //subpanelElement!!.addSpacer(5f)

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()
        val mods = updateInfo!!.hasUpdate.sorted().toMutableList()

        mods.addAll(updateInfo!!.hasNoUpdate.sorted())
        mods.addAll(updateInfo!!.failed)

        var spacing = 0f

        for (mod in mods)
        {
            var search = currentSearchText.lowercase()
            var result = FuzzySearch.extractOne(search, listOf(mod.name.lowercase()))

            var failed = false

            if (currentSearchText != "" && !mod.name.lowercase().contains(currentSearchText.lowercase())) failed = true
            if (search != "" && result.score > 60) failed = false
            if (failed) continue

            var cardPanel = LunaUIPlaceholder(true, 250f - 15 , 60f, mod, "ModsButton", modsPanel!!, modsElement!!).apply {

                if (mod.isUpdateAvailable && !mod.failedUpdateCheck())
                {
                    this.uiElement.addTooltipToPrevious(TooltipHelper("Update Available", 200f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)
                }

                this.backgroundAlpha = 0.9f
                this.borderAlpha = 0.75f

                if (selectedMod == null)
                {
                    this.darkColor = Misc.getDarkPlayerColor().brighter().brighter()
                    selectedMod = mod
                    this.setSelected()
                }

                else if (selectedMod == mod)
                {
                    this.darkColor = Misc.getDarkPlayerColor().brighter().brighter()
                    this.setSelected()
                }

                onClick {
                    if (selectedMod != mod)
                    {
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                        this.setSelected()
                        addRightPanel()
                    }
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

                    selectedMod = mod
                }
            }

            var paragraphElement = cardPanel.lunaElement!!.createUIElement(250f - 70, 60f, false)
            paragraphElement.position.inTL(2f,5f)

            cardPanel.lunaElement!!.addUIElement(paragraphElement)

            var text = ""
            if (mod.failedUpdateCheck() || mod.remoteVersion == null)
            {
                text = "${mod.name}\n" +
                        "Failed to get Version"
            }
            else if (mod.isUpdateAvailable)
            {
                text = "${mod.name}\n${mod.versionString}"
            }
            else if (mod.isLocalNewer)
            {
                text = "${mod.name}\n${mod.localVersion.version} vs ${mod.remoteVersion.version}"
            }
            else
            {
                text = "${mod.name}\n" + "${mod.remoteVersion.version}"
            }



            var para = paragraphElement.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

            if (mod.failedUpdateCheck() || mod.remoteVersion == null)
            {
                para.setHighlight("Failed to get Version")
                para.setHighlightColor(Misc.getNegativeHighlightColor())
            }
            else if (mod.isUpdateAvailable)
            {
                para.setHighlight(mod.versionString)
                para.setHighlightColor(Misc.getHighlightColor())
            }
            else if (mod.isLocalNewer)
            {
                para.setHighlight("${mod.localVersion.version} vs ${mod.remoteVersion.version}")
                para.setHighlightColor(Misc.getPositiveHighlightColor())
            }
            //para.position.inTL(0f, (30f - para.position.height / 2))

            modsElement!!.addSpacer(5f)

            spacing += cardPanel.position!!.height
        }

        unsupportedMods()

        modsPanel!!.addUIElement(modsElement)
        modsElement!!.externalScroller.yOffset = lastScroller

        addRightPanel()
    }

    fun addRightPanel()
    {

        if (rightPanel != null)
        {
            panel!!.removeComponent(rightPanel)
        }
        if (selectedMod == null) return

        var mHeight = panel!!.position.height * 0.9f

        rightPanel = panel!!.createCustomPanel(width - 240, height * 0.96f, null)
        rightPanel!!.position.inTL(240f, 23f)
        panel!!.addComponent(rightPanel)
        rightElement = rightPanel!!.createUIElement(width, height, false)
        rightPanel!!.addUIElement(rightElement)

        rightElement!!.position.inTL(0f, 0f)
        rightElement!!.addSpacer(2f)

        rightElement!!.addSpacer(100f)

        var cardpanelheight = height * 0.96f - 3
        var cardPanel = LunaUIPlaceholder(true, width - 240 - 20 , cardpanelheight , "empty", "none", rightPanel!!, rightElement!!)
        cardPanel.position!!.inTL(10f, 0f)

        var description = cardPanel.lunaElement!!.createUIElement(width - 240 - 20, height * 0.96f - 3, false)
        description.position.inTL(0f, 5f)

        description.addSpacer(10f)

        var mod = selectedMod

        var buttonX = 0f


        var color = Misc.getDarkPlayerColor().darker()
        color = Color((color.red * 0.95f).toInt(), (color.green * 0.95f).toInt(), (color.blue * 0.95f).toInt())

        //Forum Button
        var forumButton = description.addLunaElement(200f, 40f)
        var forumURL = mod!!.localVersion.updateURL

        if (forumURL != null)
        {
            var tooltipText = "Left click to open in Browser. Rightclick to copy the URL to the clipboard." +
                    "\n\nLink: $forumURL"
            forumButton.addTooltip(tooltipText, 400f, TooltipMakerAPI.TooltipLocation.BELOW, "Warning", "Link")
        }
        forumButton.backgroundColor = color

        if (forumURL != null)
        {
            forumButton.addText("Open Forum/Nexus Page", Misc.getBasePlayerColor())
            forumButton.backgroundColor = color
        }
        else
        {
            forumButton.addText("No Forum Page Linked", Misc.getBasePlayerColor())
            forumButton.backgroundColor = color.darker()
        }

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

            var ver = mod.localVersion

            if (it.eventValue == 0)
            {
                try {
                    Desktop.getDesktop().browse(URI.create(forumURL))
                } catch (ex: Exception) {

                }
            }

            if (it.eventValue == 1)
            {
                val stringSelection = StringSelection(forumURL)
                val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                clipboard.setContents(stringSelection, null)
            }
        }
        forumButton.position.inTL((cardPanel!!.position!!.width / 2) - forumButton.width - 10, 10f)

        //Download Button
        var downloadButton = description.addLunaElement(200f, 40f)
        var downloadURL = mod!!.remoteVersion?.directDownloadURL

        if (downloadURL != null)
        {
            var tooltipText = "Left click to open in Browser. Rightclick to copy the URL to the clipboard. Always be wary of downloading files from the internet." +
                    "\n\nLink: $downloadURL"
            downloadButton.addTooltip(tooltipText, 400f, TooltipMakerAPI.TooltipLocation.BELOW, "Warning", "Link")
        }

        downloadButton.backgroundColor = color


        if (downloadURL != null)
        {
            downloadButton.addText("Open Download Link", Misc.getBasePlayerColor())
            downloadButton.backgroundColor = color
        }
        else
        {
            downloadButton.addText("No Download Link", Misc.getBasePlayerColor())
            downloadButton.backgroundColor = color.darker()
        }

        /* forumButton.addText("Open Forum/Nexus Page", Misc.getBasePlayerColor())
         */
        downloadButton.centerText()
        downloadButton.onHoverEnter {
            downloadButton.playScrollSound()
            downloadButton.borderColor = Misc.getDarkPlayerColor().brighter()
        }
        downloadButton.onHoverExit {
            downloadButton.borderColor = Misc.getDarkPlayerColor()
        }

        downloadButton.onClick {
            downloadButton.playClickSound()

            if (downloadURL != null)
            {
                if (it.eventValue == 0)
                {
                    try {
                        val url: String = downloadURL
                        Desktop.getDesktop().browse(URI.create(url))
                    } catch (ex: Exception) {

                    }
                }

                if (it.eventValue == 1)
                {
                    val stringSelection = StringSelection(downloadURL)
                    val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clipboard.setContents(stringSelection, null)
                }
            }
        }
        downloadButton.position.inTL((cardPanel!!.position!!.width / 2 + 10)   , 10f)


        description.addPara("", 0f).position.inTL(20f, 80f)


        description.addPara("Name: ${mod.name}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Name")
        description.addPara("Installed Version: ${mod.localVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Installed Version")



        if (selectedMod!!.failedUpdateCheck() || selectedMod!!.remoteVersion == null)
        {
            description.addPara("Online Version: Failed to get version", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), ).apply {
                setHighlight("Online Version", "Failed to get version")
                setHighlightColors(Misc.getHighlightColor(), Misc.getNegativeHighlightColor())
            }
            description.addSpacer(5f)
            description.addPara("Error: " + selectedMod!!.errorMessage, 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }
        else
        {
            description.addPara("Online Version: ${mod.remoteVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Online Version")
        }

        if (mod.isLocalNewer)
        {
            description.addPara("Installed version is ahead of Remote version.", 0f, Misc.getPositiveHighlightColor(), Misc.getPositiveHighlightColor(), "Installed Version")
            description.addSpacer(5f)
        }
        else
        {
            description.addSpacer(20f)
        }

        description.addSpacer(5f)


        if (!selectedMod!!.failedUpdateCheck() || selectedMod!!.remoteVersion != null)
        {
            var lunaElement = description.addLunaElement(width - 240 - 60 , cardpanelheight * 0.7f).apply {
                enableTransparency = true

                //  innerElement.addSpacer(5f)

            }

            var scroller = lunaElement.elementPanel.createUIElement(width - 240 - 60 , cardpanelheight * 0.7f, true).apply {
                var text = ""
                var header = addSectionHeading("Changelog", Alignment.MID, 0f)
                header.position.setSize(width - 240 - 60, 20f)
                addSpacer(5f)

                var highlightedLines: List<String> = ArrayList()
                if (!mod.failedUpdateCheck() && mod.remoteVersion != null && mod.remoteVersion.txtChangelog != null)
                {
                    text = mod.remoteVersion.txtChangelog.trimAfter(10000)

                    highlightedLines = text.split("\n").filter { it.lowercase().trim().startsWith("version")}
                }
                else
                {
                    text = "No changelog available"
                }

                var change = addPara(text.replace("%", "%%"), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Online Version")
                change.setHighlight(*highlightedLines.toTypedArray())
                /*this.position.setSize(position.width, position.height + change.computeTextHeight(change.text))
                this.position.setSize(position.width, position.height )*/

            }

            lunaElement.elementPanel.addUIElement(scroller)
        }



        cardPanel.lunaElement!!.addUIElement(description)
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
    }

    override fun advance(amount: Float) {

        if (modsElement != null)
        {
            if (modsElement!!.externalScroller != null)
            {
                lastScroller = modsElement!!.externalScroller.yOffset
            }
        }

        if (reconstruct)
        {
            reconstruct = false
            constructLeftPanel()
        }
    }

    fun unsupportedMods()
    {
        var spacing = 0f
        for (mod in unsupportedMods)
        {
            var search = currentSearchText.lowercase()
            var result = FuzzySearch.extractOne(search, listOf(mod.name.lowercase()))

            var failed = false

            if (currentSearchText != "" && !mod.name.lowercase().contains(currentSearchText.lowercase())) failed = true
            if (search != "" && result.score > 60) failed = false
            if (failed) continue

            var cardPanel = LunaUIPlaceholder(true, 250f - 15 , 60f, mod, "ModsButton", modsPanel!!, modsElement!!).apply {

                this.uiElement.addTooltipToPrevious(TooltipHelper("This mod does not have Version Checker Support.", 300f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)

                this.backgroundAlpha = 0.9f
                this.borderAlpha = 0.75f


                onClick {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                    this.setSelected()
                    addRightPanel()
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

                    selectedMod = null
                }
            }

            var paragraphElement = cardPanel.lunaElement!!.createUIElement(250f - 70, 60f, false)
            paragraphElement.position.inTL(2f,5f)

            cardPanel.lunaElement!!.addUIElement(paragraphElement)

            var text = "${mod.name}\n" +
                    "Unsupported"


            var para = paragraphElement.addPara(text, 0f, Misc.getBasePlayerColor(), Color(150, 0, 255), "Unsupported")

            //para.position.inTL(0f, (30f - para.position.height / 2))

            modsElement!!.addSpacer(5f)

            spacing += cardPanel.position!!.height
        }
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

    override fun onClose() {
        super.onClose()

        if (handler != null)
        {
            handler!!.closeVersionUI()
        }

        //Not clearing this will cause a memory leak
        LunaUIBaseElement.selectedMap.clear()
        panelOpen = false

    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isConsumed) return@forEach
            if (isOpenedFromScript()) return@forEach
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                close()


                return@forEach
            }
        }
    }
}