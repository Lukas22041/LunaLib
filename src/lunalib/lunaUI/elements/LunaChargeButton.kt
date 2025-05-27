package lunalib.lunaUI.elements

import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

class LunaChargeButton(tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    private var currentCharge = 0f

    private var onFinishFunction: MutableList<() -> Unit> = ArrayList()


    var increaseRate = 0.010f
    var decreaseRate = 0.025f
    var finished = false
        private set

    init {

        advance {

            if (currentCharge > 1)
            {
                onFinishFunction.forEach {
                    it()
                }
                finished = true
                playClickSound()
                currentCharge = 1f
            }

            if (finished) return@advance
            if (isHeld)
            {
                currentCharge += increaseRate
            }
            else
            {
                currentCharge -= decreaseRate
            }

            currentCharge = MathUtils.clamp(currentCharge, 0f, 1.1f)

        }
    }

    fun onFinish(function: () -> Unit) {
        onFinishFunction.add(function)
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        /*var c = chargeColor
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(c.red / 255f,
            c.green / 255f,
            c.blue / 255f,
            c.alpha / 255f * (alphaMult * backgroundAlpha) * 0.5f)

        GL11.glRectf(x, y , x + (width * currentCharge), y + height)

        GL11.glEnd()
        GL11.glPopMatrix()*/

        var level = MathUtils.clamp(currentCharge, 0f, 1.0f)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        var curPos = x
        var segments = 30
        var colorPerSegment = 1f / segments
        var curColor = 1f

        GL11.glBegin(GL11.GL_QUAD_STRIP)



        for (segment in 0 .. segments)
        {

            var bc = backgroundColor.brighter().brighter()
            var c = Color((bc.red * curColor).toInt(), (bc.green * curColor).toInt(), (bc.blue * curColor).toInt())
            curColor -= colorPerSegment / 3

            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult * backgroundAlpha) * 0.9f)

            GL11.glVertex2f(curPos, y)
            GL11.glVertex2f(curPos, y + height)
            curPos += width / segments * level
        }

        GL11.glEnd()
        GL11.glPopMatrix()

    }


}