package lunalib.backend.ui.versionchecker

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.versionchecker.UpdateInfo.ModInfo
import lunalib.backend.util.getLunaString
import lunalib.lunaExtensions.TooltipMakerExtensions.addLunaElement
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Desktop
import java.net.URI
import java.util.concurrent.Future


//I dont recommend anyone to read through my UI code to learn from, its equivelant to the ramblings of an insane person, and such can only be understood by the crazy person themself.
class LunaVersionUIPanelV2() : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null

    var leftPanel: CustomPanelAPI? = null
    var leftElement: TooltipMakerAPI? = null

    var modsPanel: CustomPanelAPI? = null
    var modsElement: TooltipMakerAPI? = null

    var rightPanel: CustomPanelAPI? = null
    var rightElement: TooltipMakerAPI? = null

    private var width = 0f
    private var height = 0f

    private val indexThread = "http://fractalsoftworks.com/forum/index.php?topic=177.0"

    var currentSearchText = ""

    companion object {
        var closeCooldown = 0

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

    fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?, dialog: InteractionDialogAPI?) {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

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

        var saveButton = LunaUIButton(false, false,250f - 15, 30f,"", "Button", leftPanel!!, leftElement!!).apply {
            this.buttonText!!.text = "Mod Index"
            this.buttonText!!.position.inTL(this.buttonText!!.position.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.buttonText!!.position.height - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)

            this.uiElement.addTooltipToPrevious(TooltipHelper("Opens the Forum Mods Index in the Browser", 300f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)

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

                try {
                    Desktop.getDesktop().browse(URI.create(indexThread))
                } catch (ex: Exception) {

                }
            }
        }
        saveButton.borderAlpha = 0.5f
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

        createModsList()

    }

    fun createModsList()
    {
        if (modsPanel != null)
        {
            panel!!.removeComponent(modsPanel)
        }

        // 32 * amount of buttons + their spacers before + an extra gap between mods and the config buttons
        var space = (30f * 3f)

        modsPanel = panel!!.createCustomPanel(250f - 9 , height - space, null)
        modsPanel!!.position.inTL(0f, space)
        panel!!.addComponent(modsPanel)
        modsElement = modsPanel!!.createUIElement(250f - 9 , height - space, true)

        modsElement!!.position.inTL(0f, 0f)
        //subpanelElement!!.addSpacer(5f)

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()
        val mods = updateInfo!!.hasUpdate

        mods.addAll(updateInfo!!.hasNoUpdate)
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


                if (mod.isUpdateAvailable)
                {
                    this.uiElement.addTooltipToPrevious(TooltipHelper("Update Available", 200f, ""), TooltipMakerAPI.TooltipLocation.RIGHT)
                }

                this.backgroundAlpha = 0.9f
                this.borderAlpha = 0.75f

                if (selectedMod == null)
                {
                    selectedMod = mod
                    this.setSelected()
                }

                else if (selectedMod == mod)
                {
                    this.setSelected()
                }

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

                    selectedMod = mod
                }
            }

            var paragraphElement = cardPanel.lunaElement!!.createUIElement(250f - 70, 60f, false)
            paragraphElement.position.inTL(2f,5f)

            cardPanel.lunaElement!!.addUIElement(paragraphElement)

            var text = "${mod.name}\n${mod.remoteVersion.version}"
            if (mod.failedUpdateCheck())
            {
                text = "${mod.name}\n" +
                        "Failed to get Version"
            }

            if (mod.isUpdateAvailable)
            {
                text = "${mod.name}\n${mod.versionString}"
            }

            var para = paragraphElement.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())

            if (mod.failedUpdateCheck())
            {
                para.setHighlight("Failed to get Version")
                para.setHighlightColor(Misc.getNegativeHighlightColor())
            }
            else if (mod.isUpdateAvailable)
            {
                para.setHighlight(mod.versionString)
                para.setHighlightColor(Misc.getHighlightColor())
            }
            //para.position.inTL(0f, (30f - para.position.height / 2))

            modsElement!!.addSpacer(5f)

            spacing += cardPanel.position!!.height
        }

        modsPanel!!.addUIElement(modsElement)

        addRightPanel()
    }

    fun addRightPanel()
    {

        if (selectedMod == null) return
        if (rightPanel != null)
        {
            panel!!.removeComponent(rightPanel)
        }

        var mHeight = panel!!.position.height * 0.9f

        rightPanel = panel!!.createCustomPanel(width - 240, height * 0.96f, null)
        rightPanel!!.position.inTL(240f, 23f)
        panel!!.addComponent(rightPanel)
        rightElement = rightPanel!!.createUIElement(width, height, false)
        rightPanel!!.addUIElement(rightElement)

        rightElement!!.position.inTL(0f, 0f)
        rightElement!!.addSpacer(2f)

        rightElement!!.addSpacer(100f)

        var cardPanel = LunaUIPlaceholder(true, width - 240 - 20 , height * 0.96f - 3, "empty", "none", rightPanel!!, rightElement!!)
        cardPanel.position!!.inTL(10f, 0f)

        var description = cardPanel.lunaElement!!.createUIElement(width - 240 - 20, height * 0.96f - 3, true)
        description.position.inTL(0f, 5f)

        description.addSpacer(10f)

        var mod = selectedMod

        var buttonX = 0f

        var forumURL = mod!!.localVersion.updateURL

        var color = Misc.getDarkPlayerColor().darker()
        color = Color((color.red * 0.95f).toInt(), (color.green * 0.95f).toInt(), (color.blue * 0.95f).toInt())

        //Forum Button
        var forumButton = description.addLunaElement(200f, 40f)
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

            try {
                val url: String = ver.updateURL
                Desktop.getDesktop().browse(URI.create(url))
            } catch (ex: Exception) {

            }
        }
        forumButton.position.inTL((cardPanel!!.position!!.width / 3) - forumButton.width / 2 - 10, 10f)

        //Download Button
        var downloadButton = description.addLunaElement(200f, 40f)
        downloadButton.backgroundColor = color

        var downloadURL = mod!!.localVersion.directDownloadURL

        if (downloadURL != null)
        {
            downloadButton.addText("Open Direct Download", Misc.getBasePlayerColor())
            downloadButton.backgroundColor = color
        }
        else
        {
            downloadButton.addText("No Direct Download", Misc.getBasePlayerColor())
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

            var ver = mod.localVersion

            try {
                val url: String = ver.directDownloadURL
                Desktop.getDesktop().browse(URI.create(url))
            } catch (ex: Exception) {

            }
        }
        downloadButton.position.inTL((cardPanel!!.position!!.width / 3) + downloadButton.width / 2 + 10, 10f)


        description.addPara("", 0f).position.inTL(10f, 100f)


        description.addPara("Name: ${mod.name}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Name")
        description.addPara("Installed Version: ${mod.localVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Installed Version")
        description.addPara("Online Version: ${mod.remoteVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Online Version")

        description.addSpacer(20f)

        if (mod.remoteVersion.changelog != null)
        {
            description.addPara("Changelog:\n\n${mod.remoteVersion.changelog}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Changelog")
        }
        else
        {
            description.addPara("Changelog:\nNo Changelog Available", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Changelog")
        }

         /*for (i in 0..80)
         {
             description.addPara("Test.", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())
         }*/

        cardPanel.lunaElement!!.addUIElement(description)
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {


    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {


        if (reconstruct)
        {
            reconstruct = false
            constructLeftPanel()
        }
    }

    override fun processInput(events: MutableList<InputEventAPI>) {

        if (closeCooldown > 1)
        {
            closeCooldown--
            return
        }

        events.forEach { event ->
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                dialog!!.dismiss()

                //Not clearing this will cause a memory leak
                LunaUIBaseElement.selectedMap.clear()
                panelOpen = false

                return@forEach
            }
        }
    }
}