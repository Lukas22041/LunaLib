package lunaSettings.UIElements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f

class LunaTextField(key: Any, data: Any?, group: String, panel: TooltipMakerAPI) : LunaBaseUIElement(key, data, group, panel) {

    var borderColor = Misc.getDarkPlayerColor()

    init {
        onClick {
            if (it.eventValue == 0)
            {
                setSelected()
                Global.getSoundPlayer().playSound("ui_button_pressed", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
            }
            onClickOutside {
                unselect()
            }
        }

        onHover {
            darkColor = Misc.getDarkPlayerColor().brighter()
        }
        onNotHover {
            darkColor = Misc.getDarkPlayerColor()
        }
        onHeld {
            baseColor = Misc.getBasePlayerColor().brighter()
        }
        onNotHeld {
            baseColor = Misc.getBasePlayerColor()
        }
        onUpdate {
            if (isSelected())
            {
                borderColor = Misc.getBasePlayerColor()
            }
            else
            {
                borderColor = Misc.getDarkPlayerColor()
            }
        }
        onSelect {

        }
    }

    override fun renderBelow(alphaMult: Float) {

        if (position != null)
        {
            createGLRectangle(darkColor, alphaMult)  {
                GL11.glRectf(posX, posY, posX + width, posY + height)
            }
        }
    }

    override fun render(alphaMult: Float) {

        if (position != null)
        {
            createGLLines(borderColor, alphaMult) {

                GL11.glVertex2f(posX, posY)
                GL11.glVertex2f(posX, posY + height)
                GL11.glVertex2f(posX + width, posY + height)
                GL11.glVertex2f(posX + width, posY)
                GL11.glVertex2f(posX, posY)
            }
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)
    }


    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        if (isSelected() && paragraph != null)
        {

            var holdingShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)

            for (event in events)
            {
                if (event.isConsumed) continue
                if (event.eventValue == Keyboard.KEY_LSHIFT) continue
                if (event.isModifierKey) continue
                if (event.eventValue == Keyboard.KEY_BACK)
                {
                    if (paragraph!!.text.length != 0)
                    {
                        paragraph!!.text = paragraph!!.text.substring(0, paragraph!!.text.length - 1)
                    }
                    event.consume()
                    continue
                }
                if (event.eventValue == Keyboard.KEY_SPACE)
                {
                    paragraph!!.text += " "
                    event.consume()
                    continue
                }
                if (paragraph!!.position.width < width) return
                if (event.isKeyDownEvent)
                {
                    if (holdingShift)
                    {
                        paragraph!!.text += Keyboard.getKeyName(event.eventValue)
                    }
                    else
                    {
                        paragraph!!.text += Keyboard.getKeyName(event.eventValue).lowercase()
                    }
                }
            }
        }
    }
}