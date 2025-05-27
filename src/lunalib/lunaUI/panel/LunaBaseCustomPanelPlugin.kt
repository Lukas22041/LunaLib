package lunalib.lunaUI.panel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.CombatHandler
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaUI.LunaUIUtils
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

abstract class LunaBaseCustomPanelPlugin : BaseCustomUIPanelPlugin() {

    private var closeSpriteOff = Global.getSettings().getSprite("ui", "tripad_power_button")
    private var closeSpriteOn = Global.getSettings().getSprite("ui", "tripad_power_button_glow")
    private var closeSpriteBackground = Global.getSettings().getSprite("ui", "tripad_power_button_slot")
    private var buttonAlpha = 0f

    var enableCloseButton = false

    private var buttonLocation = Vector2f(Global.getSettings().screenWidth - closeSpriteOff.width, Global.getSettings().screenHeight - closeSpriteOff.height)

    lateinit var panel: CustomPanelAPI
    lateinit var callbacks: DialogCallbacks
    lateinit var dialog: InteractionDialogAPI

    private var openedFromExistingDialog: Boolean = false
    private var openedFromScript: Boolean = false

    fun init(panel: CustomPanelAPI, callbacks: CustomVisualDialogDelegate.DialogCallbacks, dialog: InteractionDialogAPI, openedFromExistingDialog: Boolean)
    {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        this.openedFromExistingDialog = openedFromExistingDialog


        init()
    }

    fun initFromScript(panel: CustomPanelAPI)
    {
        this.panel = panel

        this.openedFromExistingDialog = false
        this.openedFromScript = true

        var backgroundPanel = panel.createCustomPanel(panel.position.width, panel.position.height, null)
        panel.addComponent(backgroundPanel)
        var backgroundElement = backgroundPanel.createUIElement(panel.position.width, panel.position.height, false)
        backgroundPanel.addUIElement(backgroundElement)
        backgroundPanel.position.inTL(0f, 0f)
        backgroundElement.addLunaElement(panel.position.width, panel.position.height).apply {
            renderBackground = true
            renderBorder = false
            backgroundColor = Color(0, 0, 0)
            position.inTL(0f, 0f)
        }
        backgroundElement.position.inTL(0f, 0f)

        init()
    }

    abstract fun init()

    fun isOpenedFromScript() = openedFromScript

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        if (openedFromScript)
        {
          /*  var c = Color(0, 0, 0)
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)

            GL11.glDisable(GL11.GL_BLEND)

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * 1f))

            GL11.glRectf(panel.position.x, panel.position.y , panel.position.x + panel.position.width, panel.position.y + panel.position.height)

            //GL11.glEnd()
            GL11.glPopMatrix()*/
        }
    }

    override fun render(alphaMult: Float) {
        if (openedFromScript)
        {
            var c = Misc.getDarkPlayerColor()
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)

            GL11.glDisable(GL11.GL_BLEND)

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * 1f))

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(panel.position.x, panel.position.y)
            GL11.glVertex2f(panel.position.x, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y)
            GL11.glVertex2f(panel.position.x, panel.position.y)

            GL11.glEnd()
            GL11.glPopMatrix()
        }

        if (enableCloseButton)
        {
            closeSpriteBackground.render(buttonLocation.x - 32f, buttonLocation.y)
            closeSpriteBackground.alphaMult = alphaMult

            closeSpriteOff.render(buttonLocation.x, buttonLocation.y)
            closeSpriteOff.alphaMult = alphaMult

            closeSpriteOn.alphaMult = buttonAlpha * alphaMult
            closeSpriteOn.render(buttonLocation.x, buttonLocation.y)
        }
    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        if (isOpenedFromScript())
        {
            events.forEach {
                if (it.isConsumed) return@forEach
                if (it.isKeyDownEvent && it.eventValue == Keyboard.KEY_ESCAPE)
                {
                    CombatHandler.canBeRemoved = true
                    LunaUIBaseElement.selectedMap.clear()
                    it.consume()
                }
            }
        }

        if (enableCloseButton)
        {
            for (event in events) {
                if (event.isMouseEvent) {
                    if (event.x.toFloat() in buttonLocation.x..(buttonLocation.x + closeSpriteOff.width) && event.y.toFloat() in buttonLocation.y..(buttonLocation.y + closeSpriteOff.height)) {
                        buttonAlpha = MathUtils.clamp(buttonAlpha + 0.5f, 0f, 1f)
                    }
                    else
                    {
                        buttonAlpha = MathUtils.clamp(buttonAlpha - 0.5f, 0f, 1f)
                    }
                }
                if (event.isMouseDownEvent)
                {
                    if (event.x.toFloat() in buttonLocation.x..(buttonLocation.x + closeSpriteOff.width) && event.y.toFloat() in buttonLocation.y..(buttonLocation.y + closeSpriteOff.height))
                    {
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                        close()
                    }
                }
            }
        }
    }

    final fun close()
    {
        onClose()

        if (!openedFromScript)
        {
            dialog.showTextPanel()
            dialog.showVisualPanel()
            callbacks.dismissDialog()

            if (!openedFromExistingDialog)
            {
                dialog.dismiss()
            }
        }


        //Not clearing this may cause a memory leak
        LunaUIBaseElement.selectedMap.clear()
        LunaUIUtils.selectedElements.clear()


    }

    open fun onClose()
    {

    }

    final override fun buttonPressed(buttonId: Any?) {

    }
}