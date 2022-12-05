package lunalib.lunaDebug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.*
import lunalib.lunaSettings.LunaSettingsLoader
import org.lazywizard.lazylib.JSONUtils
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlinx.coroutines.*

//Probably the worst code ive ever written, it works, but at some point it should probably be rewritten.
//Do not use this as an example for your own UI.
internal class DebugWindowUI() : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: CustomVisualDialogDelegate.DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null
    private var pW = 0f
    private var pH = 0f

    private var tabs: MutableList<String> = ArrayList()
    private var tabButtons: MutableMap<String, ButtonAPI> = HashMap()
    private var selectedTab: DebugTab? = null

    companion object
    {
        private var previousTab: String = "Search"
    }

    fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?, dialog: InteractionDialogAPI?) {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        pW = this.panel!!.position.width
        pH = this.panel!!.position.height

        tabs.addAll(listOf("Search", "Items"))

        reset()
        addTabs()
    }

    fun addTabs()
    {
        var tabElement = panel!!.createUIElement(panel!!.position.width, panel!!.position.height, false)
        var heading = tabElement.addSectionHeading("Debug Menu", Alignment.MID, 0f)
        var spacing = 0f

        for (tab in tabs)
        {
            val tabButton: ButtonAPI = tabElement!!.addAreaCheckbox(tab, null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                panel!!.position.width / tabs.size,
                pH * 0.075f,
                0f)
            tabButton.position.inTL(spacing, heading.position.height)
            tabButtons.put(tab, tabButton)

            if (tab == previousTab)
            {
                tabButton.isChecked = true
            }

            spacing += panel!!.position.width / tabs.size
        }

        panel!!.addUIElement(tabElement)
    }

    private fun reset() {

    }

    private fun resetOption()
    {

    }

    override fun advance(amount: Float)
    {
        for (button in tabButtons)
        {
            if (selectedTab != null && button.key == selectedTab!!.panelName)
            {
                button.value.isChecked = false
                button.value.highlight()
            }
            else if (button.value.isChecked)
            {
                if (selectedTab != null)
                {
                    selectedTab!!.close()
                }

                when(button.key)
                {
                    "Search" -> { selectedTab = SearchTab(panel!!, dialog!!); previousTab = "Search" }
                    "Items" -> { selectedTab = ItemTab(panel!!, dialog!!); previousTab = "Items" }
                }
                selectedTab!!.init()

                button.value.isChecked = false
            }
            else
            {
                button.value.unhighlight()
            }
        }

        if (selectedTab != null)
        {
            selectedTab!!.advance(amount)
        }
    }


    override fun render(alphaMult: Float)
    {
        val playercolor = Misc.getDarkPlayerColor()
        val panelsToOutline: MutableList<CustomPanelAPI> = ArrayList()

        if (selectedTab != null)
        {
            for (outline in selectedTab!!.panelsToOutline.filterNotNull())
            {
                panelsToOutline.add(outline)
            }
        }

        for (panel in panelsToOutline)
        {
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.red.toByte(),
                playercolor.green.toByte(),
                playercolor.blue.toByte(),
                playercolor.alpha.toByte())

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(panel.position.x, panel.position.y)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x, panel.position.y)

            GL11.glEnd()
        }
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        val bgColor = Color(20, 20, 20)
        val x: Float = panel!!.position.x
        val y: Float = panel!!.position.y
        val w: Float = panel!!.position.width
        val h: Float = panel!!.position.height
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(bgColor.red / 255f, bgColor.green / 255f, bgColor.blue / 255f, bgColor.alpha / 255f * alphaMult)
        GL11.glRectf(x, y, x + w, y + h)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPopMatrix()
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!) {
            if (event.isConsumed) continue

            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()
                dialog!!.dismiss()

                continue
            }
        }
    }
}