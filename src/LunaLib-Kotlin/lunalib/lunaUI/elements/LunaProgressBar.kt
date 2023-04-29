package lunalib.lunaUI.elements

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

class LunaProgressBar(private var value: Float, private var minvalue: Float, private var maxValue: Float, textColor: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    private var prefix = ""
    private var hideNumber = false

    init {
        value = MathUtils.clamp(value, minvalue, maxValue)

        addText("$prefix$value", baseColor = textColor)
        centerText()

        renderBorder = true
        renderBackground = false
    }

    fun getValue() = value


    fun changeBoundaries(minvalue: Float, maxValue: Float)
    {
        this.minvalue = minvalue
        this.maxValue = maxValue
        changeValue(value)
    }

    fun changeValue(newValue: Float)
    {
        value = MathUtils.clamp(newValue, minvalue, maxValue)
        refreshText()
    }

    fun changePrefix(text: String)
    {
        prefix = text
        refreshText()
    }

    fun showNumber(bool: Boolean)
    {
        hideNumber = !bool
        refreshText()
    }

    fun refreshText()
    {
        if (hideNumber)
        {
            changeText("$prefix")
            centerText()
        }
        else
        {
            changeText("$prefix$value")
            centerText()
        }
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        var level = (value - minvalue) / (maxValue - minvalue)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        if (enableTransparency)
        {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
        else
        {
            GL11.glDisable(GL11.GL_BLEND)
        }

        var curPos = x
        var segments = 30
        var colorPerSegment = 1f / segments
        var curColor = 1f

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {

            var bc = backgroundColor
            var c = Color((bc.red * curColor).toInt(), (bc.green * curColor).toInt(), (bc.blue * curColor).toInt())
            curColor -= colorPerSegment / 3

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(curPos, y)
            GL11.glVertex2f(curPos, y + height)
            curPos += width / segments * level
        }

        GL11.glEnd()
        GL11.glPopMatrix()

    }
}