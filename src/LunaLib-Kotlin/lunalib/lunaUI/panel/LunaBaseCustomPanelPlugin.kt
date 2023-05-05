package lunalib.lunaUI.panel

import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.lunaUI.LunaUIUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

abstract class LunaBaseCustomPanelPlugin : BaseCustomUIPanelPlugin() {


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

        init()
    }

    abstract fun init()

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        if (openedFromScript)
        {
            var c = Color(0, 0, 0)
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)

            GL11.glDisable(GL11.GL_BLEND)

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * 1f))

            GL11.glRectf(panel.position.x, panel.position.y , panel.position.x + panel.position.width, panel.position.y + panel.position.height)

            GL11.glEnd()
            GL11.glPopMatrix()
        }
    }

    override fun render(alphaMult: Float) {
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

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>) {

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

    fun onClose()
    {

    }

    final override fun buttonPressed(buttonId: Any?) {

    }
}