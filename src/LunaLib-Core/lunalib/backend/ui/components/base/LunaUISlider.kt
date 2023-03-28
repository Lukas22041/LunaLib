package lunalib.backend.ui.components.base

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import kotlin.math.round


internal class LunaUISlider <T> (var value: T, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var borderColor = Misc.getDarkPlayerColor()
    var sliderCenterColor = Misc.getDarkPlayerColor()

    var sliderPosX = 0f
    var level = 0f


    init {

        setSliderPositionByValue(value)

        onClick {event ->
            if (event.eventValue == 0 && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height ))
            {
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
            }
        }
        onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        onHeld {event ->
            if (event.isMouseEvent && event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (posY)..(posY + height ))
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
            sliderCenterColor = Misc.getDarkPlayerColor().brighter()
        }
        onNotHover {
            sliderCenterColor = Misc.getDarkPlayerColor()
        }
    }

    fun setSliderPositionByValue(curValue: T)
    {
        var min = centerX - width / 2 + width / 20
        var max = centerX + width / 2 - width / 20
        if (curValue is Float)
        {
            var level = (curValue - minValue) / (maxValue - minValue)
            level -= 0.5f
            var scale = max - min
            sliderPosX = ((scale  * level).toFloat())
        }
        if (curValue is Int)
        {
            var level = (curValue - minValue) / (maxValue - minValue)
            level -= 0.5f
            var scale = max - min
            sliderPosX = ((scale  * level).toFloat())
        }
        if (curValue is Double)
        {
            var level = (curValue - minValue) / (maxValue - minValue)
            level -= 0.5f
            var scale = max - min
            sliderPosX = ((scale  * level).toFloat())
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        when (value)
        {
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
            }
        }
    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)


    }

    override fun renderBelow(alphaMult: Float) {
        if (position != null)
        {
            createGLRectangle(darkColor.darker(), alphaMult)  {
                GL11.glRectf(posX, posY , posX + width, posY + height)
            }
            createGLRectangle(sliderCenterColor, alphaMult)  {
                var sliderWidth = width / 20
                var sliderPos = centerX + sliderPosX
                var min = centerX - width / 2 + width / 20
                var max = centerX + width / 2 - width / 20
                sliderPos = MathUtils.clamp(sliderPos, min, max)

                GL11.glRectf(sliderPos - sliderWidth, posY , sliderPos + sliderWidth, posY + height )
            }
        }
    }

    override fun render(alphaMult: Float) {
        if (position != null)
        {
            createGLLines(darkColor, alphaMult) {

                GL11.glVertex2f(posX, posY)
                GL11.glVertex2f(posX, posY + height )
                GL11.glVertex2f(posX + width, posY + height )
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

                GL11.glVertex2f(sliderPos + sliderWidth, posY + height)
                GL11.glVertex2f(sliderPos + sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY)
                GL11.glVertex2f(sliderPos - sliderWidth, posY + height)
                GL11.glVertex2f(sliderPos + sliderWidth, posY + height)
            }
        }
    }
}

