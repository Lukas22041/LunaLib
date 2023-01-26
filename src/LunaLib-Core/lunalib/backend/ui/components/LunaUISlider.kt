package lunalib.backend.ui.components

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import kotlin.math.round


class LunaUISlider <T> (var value: T, var hasParagraph: Boolean, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var borderColor = Misc.getDarkPlayerColor()
    var sliderCenterColor = Misc.getDarkPlayerColor().brighter()

    var sliderPosX = 0f
    var level = 0f
    var sliderParagraph: LabelAPI? = null

    var yOffset = 1

    init {

        if (hasParagraph == true)
        {
            yOffset = 2
        }

        when (value) {
            is Color -> {
                var col = value as Color
                var array = Color.RGBtoHSB(col.red, col.green, col.blue, null)
                var hue = array.get(0)

                var level = (hue - minValue) / (maxValue - minValue)
                level -= 0.5f
                var scale = width

                sliderPosX = ((width)  * level).toFloat()
            }
            is Int -> {
                var level = (value as Int - minValue) / (maxValue - minValue)
                level -= 0.5f
                var scale = width

                sliderPosX = ((width)  * level).toFloat()
            }
            is Double -> {
                var level = (value as Double - minValue) / (maxValue - minValue)
                level -= 0.5f
                var scale = width

                sliderPosX = ((width)  * level).toFloat()
            }
        }

        onClick {event ->
            if (event.eventValue == 0 && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height / yOffset))
            {
                Global.getSoundPlayer().playSound("ui_button_pressed", 1f, 1f, Vector2f(0f, 0f), Vector2f(0f, 0f))
            }
        }
        onHeld {event ->
            if (event.isMouseEvent && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height / yOffset))
            {
                setSelected()
                borderColor = Misc.getDarkPlayerColor().brighter().brighter().brighter()
                sliderPosX = event.x.toFloat() - position!!.centerX
                event.consume()
            }
        }
        onNotHeld {
            unselect()
            borderColor = Misc.getDarkPlayerColor().brighter()
        }
        onHover {event ->
             if (event.x.toFloat() in ((sliderPosX + centerX) - width / 20)..((sliderPosX + centerX)+ width / 20) && event.y.toFloat() in (posY)..(posY + height / yOffset))
            {
                sliderCenterColor = Misc.getDarkPlayerColor().brighter().brighter()
            }
            else
            {
                sliderCenterColor = Misc.getDarkPlayerColor().brighter()
            }
        }
        onNotHover {
            sliderCenterColor = Misc.getDarkPlayerColor().brighter()
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (sliderParagraph != null || !hasParagraph)
        {
            when (value)
            {
                is Color -> {
                    var color = Color.getHSBColor(level, 1f, 1f)
                    if (hasParagraph)
                    {
                        sliderParagraph!!.setHighlight(sliderParagraph!!.text)
                        sliderParagraph!!.setHighlightColor(color!!)
                    }
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

                    if (hasParagraph)
                    {
                        sliderParagraph!!.text = "Value: $value"
                        sliderParagraph!!.setHighlight("Value:")
                        sliderParagraph!!.setHighlightColor(Misc.getHighlightColor())
                    }
                }
            }

        }
    }


    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (hasParagraph)
        {
            if (sliderParagraph == null && lunaElement != null)
            {
                var pan = lunaElement!!.createUIElement(width, height, false)
                uiElement.addComponent(pan)
                lunaElement!!.addUIElement(pan)
                pan.position.inTL(0f, 0f)
                sliderParagraph = pan.addPara("Color", 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            }

            if (sliderParagraph != null)
            {
                sliderParagraph!!.position.inTL((width / 2 - sliderParagraph!!.computeTextWidth(sliderParagraph!!.text) / 2) , 0f )
            }
        }
    }

    override fun renderBelow(alphaMult: Float) {
        if (position != null)
        {
            createGLRectangle(darkColor.darker(), alphaMult)  {
                GL11.glRectf(posX, posY , posX + width, posY + height / yOffset)
            }
            createGLRectangle(sliderCenterColor, alphaMult)  {
                var sliderWidth = width / 20
                var sliderPos = centerX + sliderPosX
                var min = centerX - width / 2 + width / 20
                var max = centerX + width / 2 - width / 20
                sliderPos = MathUtils.clamp(sliderPos, min, max)

                GL11.glRectf(sliderPos - sliderWidth, posY , sliderPos + sliderWidth, posY + height / yOffset)
            }

            if (!hasParagraph && value is Color)
            {
                createGLRectangle(value as Color, alphaMult)  {
                    GL11.glRectf(posX + width + 5, posY , posX + width + width / 8, posY + height / yOffset)
                }
            }
        }
    }

    override fun render(alphaMult: Float) {
        if (position != null)
        {
            createGLLines(darkColor, alphaMult) {

                GL11.glVertex2f(posX, posY)
                GL11.glVertex2f(posX, posY + height / yOffset)
                GL11.glVertex2f(posX + width, posY + height / yOffset)
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

                GL11.glVertex2f(sliderPos + sliderWidth, posY + height / yOffset)
                GL11.glVertex2f(sliderPos + sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY + height / yOffset)
                GL11.glVertex2f(sliderPos + sliderWidth, posY + height / yOffset)
            }
        }
    }
}

