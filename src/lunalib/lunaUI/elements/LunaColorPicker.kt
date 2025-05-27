package lunalib.lunaUI.elements

import com.fs.starfarer.api.ui.TooltipMakerAPI
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

class LunaColorPicker(private var hue: Float, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {


    init {
        centerText()
        hue = MathUtils.clamp(hue, 0f, 1f)

        renderBorder = false
        renderBackground = false

        onClick {
            playClickSound()
        }
        onHeld {
            var clickX = it.x - x

            var level = (clickX - 0f) / (width - 0f)
            level = MathUtils.clamp(level, 0f, 1f)
            hue = level
        }
    }

    fun getColor(): Color
    {
        return Color.getHSBColor(hue, 1f, 1f)
    }

    private fun drawCircle(radius: Float, posX: Float, posY: Float, color: Color, alphaMult: Float, blend: Boolean = false)
    {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        if (blend)
        {
            GL11.glEnable(GL11.GL_BLEND)
        }
        else
        {
            GL11.glDisable(GL11.GL_BLEND)
        }
        /*var posX = x + (width * value)
        posX = MathUtils.clamp(posX, x + 2f, x + width - 2f)

        var posY = y + height / 2*/

        var r = radius
        GL11.glBegin(GL11.GL_TRIANGLE_FAN)

        var c = color
        GL11.glColor4f(c.red / 255f,
            c.green / 255f,
            c.blue / 255f,
            c.alpha / 255f * (alphaMult * backgroundAlpha))

        for (i in 0..360)
        {
            GL11.glVertex2d(r*Math.cos(Math.PI * i / 180.0) + posX, r*Math.sin(Math.PI * i / 180.0) + posY)
        }

        GL11.glEnd()
        GL11.glPopMatrix()
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)



        drawCircle(height * 0.75f,x + (width * hue), y + height / 2 , Color(50, 50, 50, 50), alphaMult, blend = true)
        drawCircle(height * 0.75f - 2,x + (width * hue), y + height / 2 , getColor(), alphaMult)


    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)




        //Render bar
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
        var curColor = 0f

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {
            var color = Color.getHSBColor(curColor, 1f, 1f)
            curColor += colorPerSegment
            GL11.glColor4f(color.red / 255f,
                color.green / 255f,
                color.blue / 255f,
                color.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(curPos, y )
            GL11.glVertex2f(curPos, y + height)
            curPos += width / segments
        }


        // GL11.glRectf(x, y , x + width * level, y + height)

        GL11.glEnd()
        GL11.glPopMatrix()

    }
}