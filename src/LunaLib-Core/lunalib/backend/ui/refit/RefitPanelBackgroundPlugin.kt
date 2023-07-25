package lunalib.backend.ui.refit

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.refit.RefitButtonAdder.Companion.removeActivePanel
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class RefitPanelBackgroundPlugin(private var parent: UIPanelAPI, var forButton: Boolean) : CustomUIPanelPlugin {

    var position: PositionAPI? = null
    var panel: UIPanelAPI? = null


    override fun positionChanged(position: PositionAPI?) {
        this.position = position
    }

    override fun renderBelow(alphaMult: Float) {
        if (position == null) return
        var c = Color(0, 0, 0)
       /* if (CustomExoticaPanel.renderDefaultBackground())
        {*/
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)

            GL11.glDisable(GL11.GL_BLEND)

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * 1f))

            GL11.glRectf(position!!.x,position!!.y , position!!.x + position!!.width, position!!.y + position!!.height)

            //GL11.glEnd()
            GL11.glPopMatrix()
      //  }

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(c.red / 255f,
            c.green / 255f,
            c.blue / 255f,
            c.alpha / 255f * (alphaMult * 0.5f))

        GL11.glRectf(0f, 0f, Global.getSettings().screenWidth, Global.getSettings().screenHeight)

        //GL11.glEnd()
        GL11.glPopMatrix()
    }

    override fun render(alphaMult: Float) {
        if (position == null) return
      //  if (!CustomExoticaPanel.renderDefaultBorder()) return

        var c = Misc.getDarkPlayerColor()
        GL11.glPushMatrix()

        GL11.glTranslatef(0f, 0f, 0f)
        GL11.glRotatef(0f, 0f, 0f, 1f)

        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(c.red / 255f,
            c.green / 255f,
            c.blue / 255f,
            c.alpha / 255f * (alphaMult * 1f))

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINE_STRIP)

        var x = position!!.x
        var y = position!!.y
        var width = position!!.width
        var height = position!!.height

        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x, y + height)
        GL11.glVertex2f(x + width, y + height)
        GL11.glVertex2f(x + width, y)
        GL11.glVertex2f(x, y)

        GL11.glEnd()
        GL11.glPopMatrix()
    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!)
        {
            if (event.isConsumed) continue
            if (panel == null) continue
            if (event.isKeyUpEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()
                close()
                continue
            }
            if (event.isKeyboardEvent)
            {
                event.consume()
            }

            if (event.isMouseDownEvent) {
                if (event.x.toFloat() !in panel!!.position.x..panel!!.position.x + panel!!.position.width ||
                    event.y.toFloat() !in panel!!.position.y..panel!!.position.y + panel!!.position.height) {
                    close()
                    event.consume()
                }
            }

            if (event.isMouseMoveEvent || event.isMouseDownEvent || event.isMouseScrollEvent)
            {

                event.consume()
            }
         //   event.consume()
        }
    }

    override fun buttonPressed(buttonId: Any?) {

    }

    fun close() {
        if (forButton) {
            RefitButtonAdder.removeActivePanel = true
        }
        else {
            parent.removeComponent(panel)
        }
    }

}