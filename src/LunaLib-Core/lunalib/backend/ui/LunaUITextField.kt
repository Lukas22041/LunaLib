package lunalib.backend.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import java.util.regex.Pattern


enum class Filters {
    None, Double, Int
}

class LunaUITextField(width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI, var filter: Filters) : LunaBaseUIElement(width, height, key, group, panel, uiElement) {

    var borderColor = Misc.getDarkPlayerColor()

    init {
        onClick {
            if (it.eventValue == 0)
            {
                setSelected()
                Global.getSoundPlayer().playSound("ui_button_pressed", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
            }
        }
        onClickOutside {
            unselect()
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

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (paragraph == null && lunaElement != null)
        {
            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)
            paragraph = pan.addPara("Test", 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        }

        if (paragraph != null)
        {
            paragraph!!.position.inTL(5f, height / 2 - paragraph!!.position.height / 2)
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

    var isCooldown = false
    var cooldown = 5f
    val Digits = "(\\p{Digit}+)"
    val HexDigits = "(\\p{XDigit}+)"

    val DOUBLE_PATTERN =
        Pattern.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" + "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" + "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" + "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*")

    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        if (isCooldown)
        {
            cooldown--
            if (cooldown < 0)
            {
                isCooldown = false
            }
            return
        }

        if (isSelected() && paragraph != null)
        {

            for (event in events) {
                if (event.isConsumed) continue
                if (event.eventValue == Keyboard.KEY_ESCAPE || event.eventValue == 28 || event.eventValue == 156)
                {
                    unselect()
                    event.consume()
                    Global.getSoundPlayer().playSound("ui_typer_buzz", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                    continue
                }
                if (event.eventValue == Keyboard.KEY_LSHIFT) continue
                if (event.eventValue == Keyboard.KEY_LCONTROL && Keyboard.isKeyDown(Keyboard.KEY_BACK))
                {
                    paragraph!!.text = ""
                    event.consume()
                }
                if (event.isModifierKey) continue
                if (event.eventValue == Keyboard.KEY_BACK)
                {
                    if (paragraph!!.text.length != 0)
                    {
                        paragraph!!.text = paragraph!!.text.substring(0, paragraph!!.text.length - 1)
                        isCooldown = true
                        Global.getSoundPlayer().playSound("ui_typer_type", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                        cooldown = 4f
                    }
                    event.consume()
                    continue
                }
                if (paragraph!!.computeTextWidth(paragraph!!.text) > width - 20)
                {
                    Global.getSoundPlayer().playSound("ui_typer_buzz", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                    continue
                }
                if (event.eventValue == 15) continue
                if (event.isKeyboardEvent && !event.isKeyUpEvent)
                {
                    var char = event.eventChar
                    if (char != '0' && char != '&' && char != '%')
                    {
                        if (filter == Filters.None)
                        {
                            paragraph!!.text += char
                            isCooldown = true
                            cooldown = 1f
                            event.consume()
                            Global.getSoundPlayer().playSound("ui_typer_type", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                            break
                        }
                        else if (filter == Filters.Double)
                        {
                            if (DOUBLE_PATTERN.matcher(paragraph!!.text + char).matches() || char == '-' && paragraph!!.text.isEmpty())
                            {
                                paragraph!!.text += char
                                paragraph!!.text = paragraph!!.text.replace("[^0-9.-]".toRegex(), "")
                                isCooldown = true
                                cooldown = 1f
                                event.consume()
                                Global.getSoundPlayer().playSound("ui_typer_type", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                                break
                            }
                            else
                            {
                                isCooldown = true
                                cooldown = 1f
                                event.consume()
                                Global.getSoundPlayer().playSound("ui_typer_buzz", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                                break
                            }
                        }
                        else if (filter == Filters.Int)
                        {
                            isCooldown = true
                            cooldown = 1f
                            event.consume()
                            if (char == '-' && paragraph!!.text.isNotEmpty())
                            {
                                Global.getSoundPlayer().playSound("ui_typer_buzz", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                                break
                            }
                            paragraph!!.text += char
                            if (paragraph!!.text.contains("[^0-9-]".toRegex()))
                            {
                                paragraph!!.text = paragraph!!.text.replace("[^0-9-]".toRegex(), "")
                                Global.getSoundPlayer().playSound("ui_typer_buzz", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                            }
                            else
                            {
                                Global.getSoundPlayer().playSound("ui_typer_type", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
                            }
                            break
                        }
                    }
                }
            }
        }
    }
}