package lunalib.backend.ui.components

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f

class LunaUIButton(var value: Boolean, var regularButton: Boolean, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var buttonText: LabelAPI? = null
    var borderColor = Misc.getDarkPlayerColor()
    var backgroundColor = Misc.getDarkPlayerColor()

    init {

        onClick {
            if (it.eventValue == 0)
            {
                setSelected()
                value = !value
                Global.getSoundPlayer().playSound("ui_button_pressed", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                unselect()
            }
        }

        onHover {
            borderColor = Misc.getDarkPlayerColor().brighter()
        }
        onNotHover {
            borderColor = Misc.getDarkPlayerColor()
        }
        onHeld {
            borderColor = Misc.getDarkPlayerColor().brighter().brighter()
        }
        onNotHeld {
            baseColor = Misc.getBasePlayerColor()
            borderColor = Misc.getDarkPlayerColor()
        }
    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (buttonText == null && lunaElement != null)
        {
            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)
            buttonText = pan.addPara("", 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        }

        if (buttonText != null)
        {
            buttonText!!.position.inTL(width / 2 - buttonText!!.computeTextWidth(buttonText!!.text) / 2, height / 2 - buttonText!!.computeTextHeight(buttonText!!.text) / 2)
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (buttonText != null)
        {
            if (regularButton)
            {
                buttonText!!.text = value.toString().capitalize()
                buttonText!!.position.inTL(width / 2 - buttonText!!.computeTextWidth(buttonText!!.text) / 2, height / 2 - buttonText!!.computeTextHeight(buttonText!!.text) / 2)
            }
        }
        if (!value) backgroundColor = Misc.getDarkPlayerColor().darker()
        else backgroundColor = Misc.getDarkPlayerColor()
    }

    override fun renderBelow(alphaMult: Float) {
        createGLRectangle(backgroundColor, alphaMult) {
            GL11.glRectf(posX, posY , posX + width, posY + height)
        }
    }

    override fun render(alphaMult: Float) {
        createGLLines(borderColor, alphaMult) {

            GL11.glVertex2f(posX, posY)
            GL11.glVertex2f(posX, posY + height)
            GL11.glVertex2f(posX + width, posY + height)
            GL11.glVertex2f(posX + width, posY)
            GL11.glVertex2f(posX, posY)
        }
    }
}