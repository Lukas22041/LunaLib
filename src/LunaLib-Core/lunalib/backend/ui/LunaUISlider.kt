package lunalib.backend.ui

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.opengl.GL11

class LunaUISlider(var width: Float, var height: Float) : CustomUIPanelPlugin
{

    var position: PositionAPI? = null
    var sliderPositionX = 0f
    var heldDown = false

    var onClickFunctions: MutableList<LunaUISlider.() -> Unit> = ArrayList()
        private set
    var onHeldFunctions: MutableList<LunaUISlider.() -> Unit> = ArrayList()
        private set

    override fun positionChanged(position: PositionAPI?) {
        this.position = position
    }

    override fun renderBelow(alphaMult: Float) {
    }

    fun onClick(function: LunaUISlider.() -> Unit)
    {
        onClickFunctions.add(function)
    }

    fun onHeld(function: LunaUISlider.() -> Unit)
    {
        onHeldFunctions.add(function)
    }

    override fun render(alphaMult: Float) {
        val playercolor = Misc.getDarkPlayerColor()

        if (position != null)
        {
            //Box
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.red.toByte(), playercolor.green.toByte(), playercolor.blue.toByte(), playercolor.alpha.toByte())

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(position!!.centerX + width, position!!.centerY + height)
            GL11.glVertex2f(position!!.centerX + width, position!!.centerY - height)
            GL11.glVertex2f(position!!.centerX - width, position!!.centerY - height)
            GL11.glVertex2f(position!!.centerX - width, position!!.centerY + height)
            GL11.glVertex2f(position!!.centerX + width, position!!.centerY + height)

            GL11.glEnd()


            //Slider
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.red.toByte(), playercolor.green.toByte(), playercolor.blue.toByte(), playercolor.alpha.toByte())

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            var sliderPos = position!!.centerX + sliderPositionX
            var sliderWidth = width / 20
            GL11.glVertex2f(sliderPos + sliderWidth, position!!.centerY + height)
            GL11.glVertex2f(sliderPos + sliderWidth, position!!.centerY - height)
            GL11.glVertex2f(sliderPos - sliderWidth, position!!.centerY - height)
            GL11.glVertex2f(sliderPos - sliderWidth, position!!.centerY + height)
            GL11.glVertex2f(sliderPos + sliderWidth, position!!.centerY + height)

            GL11.glEnd()
        }
    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>?)
    {
        for (event in events!!)
        {
            if (event.isMouseDownEvent && position != null)
            {
                if (event.x.toFloat() in (position!!.centerX - width)..(position!!.centerX + width) && event.y.toFloat() in (position!!.centerY - height)..(position!!.centerY + height))
                {
                    sliderPositionX = event.x.toFloat() - position!!.centerX
                    event.consume()
                    heldDown = true
                    for (onClick in onClickFunctions)
                    {
                        onClick()
                    }
                }
            }
            else if (event.isMouseUpEvent)
            {
                heldDown = false
            }
            else if (event.isMouseEvent && heldDown)
            {
                sliderPositionX = event.x.toFloat() - position!!.centerX
                event.consume()
                for (onClick in onHeldFunctions)
                {
                    onClick()
                }
            }
        }
    }
}