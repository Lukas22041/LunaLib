package lunalib.backend.ui.versionchecker

import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.util.TooltipHelper
import lunalib.backend.util.getLunaString
import lunalib.lunaExtensions.TooltipMakerExtensions.addLunaElement
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Desktop
import java.net.URI
import java.util.concurrent.Future


//I dont recommend anyone to read through my UI code to learn from, its equivelant to the ramblings of an insane person, and such can only be understood by the crazy person themself.
class LunaVersionUIPanel() : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    private var width = 0f
    private var height = 0f

    private val indexThread = "http://fractalsoftworks.com/forum/index.php?topic=177.0"

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

        constructPanel()
    }

    fun constructPanel()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }

        subpanel = panel!!.createCustomPanel(width, height, null)
        subpanel!!.position.inTL(0f, 20f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width, height, false)
        subpanel!!.addUIElement(subpanelElement)

        subpanelElement!!.position.inTL(0f, 0f)


        var element = subpanelElement!!.addLunaElement(width - 20, height - 40).apply {
            backgroundColor = Misc.getDarkPlayerColor().darker().darker()
            renderBackground = false
            renderBorder = false
        }
        element.position.inTL(10f, 10f)

        //subpanelElement!!.addPara("", 0f).position.inTL(15f, 15f)

        var inner = element.elementPanel.createUIElement(width - 20, height - 40, true)
        inner.position.inTL(0f, 0f)


        inner.addLunaElement(width - 30f, 120f).apply {
            this.innerElement.addSpacer(5f)

            addText("This Menu displays available updates for each enabled mod that integrates this system. \nSorted based on mods with available updates to mods without any." +
                    "\n\n" +
                    "Mods with Updates: ${updateInfo!!.hasUpdate.size}\n" +
                    "Mods without Updates: ${updateInfo!!.hasNoUpdate.size}", Misc.getBasePlayerColor(), Misc.getHighlightColor())
            position.inTL(5f, 5f)

            this.innerElement.addSpacer(10f)
            var color = Misc.getDarkPlayerColor().darker()

            color = Color((color.red * 0.95f).toInt(), (color.green * 0.95f).toInt(), (color.blue * 0.95f).toInt())
            var button = this.innerElement.addLunaElement(200f, 30f)
            button.backgroundColor = color
            button.addText("Open Mod Index Page", Misc.getBasePlayerColor())
            button.centerText()

            button.onHoverEnter {
                playScrollSound()
                button.borderColor = Misc.getDarkPlayerColor().brighter()
            }
            button.onHoverExit {
                button.borderColor = Misc.getDarkPlayerColor()
            }

            button.onClick {
                playClickSound()

                try {
                    Desktop.getDesktop().browse(URI.create(indexThread))
                } catch (ex: Exception) {

                }
            }
        }

        inner.addSpacer(5f)

        if (updateInfo != null)
        {
            var modsWithUpdates = updateInfo!!.hasUpdate
            var modsWithoutUpdates = updateInfo!!.hasNoUpdate
            var failedMods = updateInfo!!.failed

            for (mod in modsWithUpdates)
            {
                var updateURL = mod.localVersion.updateURL
                var buttonHeight = 0f
                if (updateURL != null) buttonHeight = 30f

                inner.addLunaElement(width - 30f, 60f + buttonHeight).apply {
                    this.backgroundColor = Misc.getDarkPlayerColor().darker().darker()
                    this.innerElement.addSpacer(5f)
                    this.innerElement.addPara("${mod.name} (Update Available)", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "Update Available")
                    this.innerElement.addSpacer(2f)
                    this.innerElement.addPara("Installed Version: ${mod.localVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "${mod.localVersion.version}")
                    this.innerElement.addPara("Latest Version: ${mod.remoteVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "${mod.remoteVersion.version}")

                    if (mod.remoteVersion.directDownloadURL != null)
                    {
                        this.innerElement.addPara("Test: ${mod.remoteVersion.directDownloadURL}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "${mod.remoteVersion.version}")
                    }

                    if (updateURL != null)
                    {
                        this.innerElement.addSpacer(3f)
                        var color = Misc.getDarkPlayerColor().darker()

                        color = Color((color.red * 0.95f).toInt(), (color.green * 0.95f).toInt(), (color.blue * 0.95f).toInt())
                        var button = this.innerElement.addLunaElement(200f, 30f)
                        button.backgroundColor = color
                        button.addText("Open Forum/Nexus Page", Misc.getBasePlayerColor())
                        button.centerText()

                        button.onHoverEnter {
                            playScrollSound()
                            button.borderColor = Misc.getDarkPlayerColor().brighter()
                        }
                        button.onHoverExit {
                            button.borderColor = Misc.getDarkPlayerColor()
                        }

                        button.onClick {
                            playClickSound()

                            var ver = mod.localVersion

                            try {
                                val url: String = ver.updateURL
                                Desktop.getDesktop().browse(URI.create(url))
                            } catch (ex: Exception) {

                            }
                        }
                    }
                }

                inner.addSpacer(5f)
            }

            for (mod in modsWithoutUpdates)
            {
                var updateURL = mod.localVersion.updateURL
                var buttonHeight = 0f
                if (updateURL != null) buttonHeight = 30f

                inner.addLunaElement(width - 30f, 60f + buttonHeight).apply {
                    this.backgroundColor = Misc.getDarkPlayerColor().darker().darker()
                    this.innerElement.addSpacer(5f)
                    this.innerElement.addPara("${mod.name}", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "")
                    this.innerElement.addSpacer(2f)
                    this.innerElement.addPara("Installed Version: ${mod.localVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), "")
                    this.innerElement.addPara("Latest Version: ${mod.remoteVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getPositiveHighlightColor(), "")

                    if (updateURL != null)
                    {
                        this.innerElement.addSpacer(3f)
                        var color = Misc.getDarkPlayerColor().darker()

                        color = Color((color.red * 0.95f).toInt(), (color.green * 0.95f).toInt(), (color.blue * 0.95f).toInt())
                        var button = this.innerElement.addLunaElement(200f, 30f)
                        button.backgroundColor = color
                        button.addText("Open Forum/Nexus Page", Misc.getBasePlayerColor())
                        button.centerText()

                        button.onHoverEnter {
                            playScrollSound()
                            button.borderColor = Misc.getDarkPlayerColor().brighter()
                        }
                        button.onHoverExit {
                            button.borderColor = Misc.getDarkPlayerColor()
                        }

                        button.onClick {
                            playClickSound()

                            var ver = mod.localVersion

                            try {
                                val url: String = ver.updateURL
                                Desktop.getDesktop().browse(URI.create(url))
                            } catch (ex: Exception) {

                            }
                        }
                    }
                }

                inner.addSpacer(5f)
            }

            for (mod in failedMods)
            {
                inner.addLunaElement(width - 30f, 60f).apply {
                    this.backgroundColor = Misc.getDarkPlayerColor().darker().darker()
                    this.innerElement.addSpacer(5f)
                    this.innerElement.addPara("${mod.name} (Error)", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), "(error)")
                    this.innerElement.addSpacer(2f)
                    this.innerElement.addPara("Installed Version: ${mod.localVersion.version}", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), "")
                    this.innerElement.addPara("Latest Version: Failed to get Version", 0f, Misc.getBasePlayerColor(), Misc.getPositiveHighlightColor(), "")
                }

                inner.addSpacer(5f)
            }

            for (mod in unsupportedMods)
            {
                inner.addLunaElement(width - 30f, 60f).apply {
                    this.backgroundColor = Misc.getDarkPlayerColor().darker().darker()
                    this.innerElement.addSpacer(5f)
                    this.innerElement.addPara("${mod.name} (Unsupported)", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), "(Unsupported)")
                    this.innerElement.addSpacer(2f)
                    this.innerElement.addPara("Installed Version: ${mod.version}", 0f, Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor(), "")
                    this.innerElement.addPara("Latest Version: Unsupported", 0f, Misc.getBasePlayerColor(), Misc.getPositiveHighlightColor(), "")
                }

                inner.addSpacer(5f)
            }
        }
        else
        {
            inner!!.addPara("Missing Version Data", 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())
        }

        inner.addSpacer(10f)
        element.elementPanel.addUIElement(inner)

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
            constructPanel()
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