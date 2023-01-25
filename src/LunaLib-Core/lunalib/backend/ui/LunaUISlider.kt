package lunalib.backend.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round


class LunaUISlider <T> (var value: T, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaBaseUIElement(width, height, key, group, panel, uiElement) {

    var borderColor = Misc.getDarkPlayerColor()
    var sliderPosX = 0f
    var level = 0f

    init {
        onClick {event ->
            if (event.eventValue == 0 && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height / 2))
            {
                Global.getSoundPlayer().playSound("ui_button_pressed", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
            }
        }
        onHeld {event ->
            if (event.isMouseEvent && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height / 2))
            {
                setSelected()
                borderColor = Misc.getBrightPlayerColor().darker()
                sliderPosX = event.x.toFloat() - position!!.centerX
                event.consume()
            }
        }
        onNotHeld {
            unselect()
            borderColor = Misc.getDarkPlayerColor().brighter()
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (paragraph != null)
        {
            when (value)
            {
                is Color -> {
                    var color = Color.getHSBColor(level, 1f, 1f)
                    paragraph!!.setHighlight(paragraph!!.text)
                    paragraph!!.setHighlightColor(color!!)
                    value = color as T
                }
                is Number -> {
                    var scale = (((maxValue - minValue) * (level - 0) / (1 - 0) ) + minValue)
                    if (value is Int)
                    {
                        value = scale.toInt() as T
                    }
                    else if (value is Double || value is Float)
                    {
                        var multiplier = 1.0
                        repeat(2) { multiplier *= 10 }
                        value = (round(scale * multiplier) / multiplier) as T
                    }
                    else
                    {
                        value = scale as T
                    }

                    paragraph!!.text = "Value: $value"
                    paragraph!!.setHighlight("Value:")
                    paragraph!!.setHighlightColor(Misc.getHighlightColor())
                }
            }

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
            paragraph = pan.addPara("Color", 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        }

        if (paragraph != null)
        {
            paragraph!!.position.inTL((width / 2 - paragraph!!.computeTextWidth(paragraph!!.text) / 2) , 0f )
        }
    }

    override fun renderBelow(alphaMult: Float) {
        if (position != null)
        {
            createGLRectangle(darkColor, alphaMult)  {
                GL11.glRectf(posX, posY , posX + width, posY + height / 2)
            }
            createGLRectangle(darkColor.brighter(), alphaMult)  {
                var sliderWidth = width / 20
                var sliderPos = centerX + sliderPosX
                var min = centerX - width / 2 + width / 20
                var max = centerX + width / 2 - width / 20
                sliderPos = MathUtils.clamp(sliderPos, min, max)

                GL11.glRectf(sliderPos - sliderWidth, posY , sliderPos + sliderWidth, posY + height / 2)
            }
        }
    }

    override fun render(alphaMult: Float) {
        if (position != null)
        {
            createGLLines(darkColor, alphaMult) {

                GL11.glVertex2f(posX, posY)
                GL11.glVertex2f(posX, posY + height / 2)
                GL11.glVertex2f(posX + width, posY + height / 2)
                GL11.glVertex2f(posX + width, posY)
                GL11.glVertex2f(posX, posY)
            }

            createGLLines(borderColor, alphaMult) {
                var sliderPos = centerX + sliderPosX
                var min = centerX - width / 2 + width / 20
                var max = centerX + width / 2 - width / 20
                sliderPos = MathUtils.clamp(sliderPos, min, max)

                var sliderWidth = width / 20
                level = (sliderPos - min) / (max - min)

                GL11.glVertex2f(sliderPos + sliderWidth, posY + height / 2)
                GL11.glVertex2f(sliderPos + sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY + height / 2)
                GL11.glVertex2f(sliderPos + sliderWidth, posY + height / 2)
            }
        }
    }
}

