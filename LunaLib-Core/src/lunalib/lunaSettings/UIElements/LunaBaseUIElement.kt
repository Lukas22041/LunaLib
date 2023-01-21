package lunaSettings.UIElements

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.WeakHashMap

abstract class LunaBaseUIElement(var key: Any, var data: Any?, var group: String, var panel: TooltipMakerAPI) : CustomUIPanelPlugin
{
    companion object {
        var selectedMap: MutableMap<String, LunaBaseUIElement?> = WeakHashMap()
    }

    private var onClickFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onClickOutsideFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onHeldFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onNotHeldFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onHoverFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onNotHoverFunctions: MutableList<LunaBaseUIElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onUpdateFunction: MutableList<LunaBaseUIElement.(List<InputEventAPI>) -> Unit> = ArrayList()
    private var onSelectFunction: MutableList<LunaBaseUIElement.() -> Unit> = ArrayList()

    var paragraph: LabelAPI? = null

    var position: PositionAPI? = null

    var posX: Float = 0f
        private set
    var posY: Float = 0f
        private set

    var centerX: Float = 0f
        private set
    var centerY: Float = 0f
        private set

    var width: Float = 0f
        private set
    var height: Float = 0f
        private set

    var baseColor = Misc.getBasePlayerColor()
    var brightColor = Misc.getBrightPlayerColor()
    var darkColor = Misc.getDarkPlayerColor()

    var isHeld = false
    var isHovering = false

    fun isSelected() : Boolean
    {
        if (selectedMap.get(group) == null) return false
        if (selectedMap.get(group) == this) return true
        return false
    }

    fun onClick(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onClickFunctions.add(function)
    }
    fun onClickOutside(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onClickOutsideFunctions.add(function)
    }

    fun onHeld(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onHeldFunctions.add(function)
    }
    fun onNotHeld(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onNotHeldFunctions.add(function)
    }


    fun onHover(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onHoverFunctions.add(function)
    }
    fun onNotHover(function: LunaBaseUIElement.(InputEventAPI) -> Unit)
    {
        onNotHoverFunctions.add(function)
    }

    fun onUpdate(function: LunaBaseUIElement.(List<InputEventAPI>) -> Unit)
    {
        onUpdateFunction.add(function)
    }

    fun onSelect(function: LunaBaseUIElement.() -> Unit)
    {
        onSelectFunction.add(function)
    }

    fun setSelected()
    {
        selectedMap.set(group, this)
        for (onSelect in onSelectFunction)
        {
            onSelect()
        }
    }

    fun unselect()
    {
        selectedMap.set(group, null)
    }


    fun addParagraph(text: String, color: Color) : LabelAPI?
    {
        paragraph = panel.addPara("Test", 0f, color, color)
        return paragraph
    }

    override fun positionChanged(position: PositionAPI) {
        this.position = position

        width = position.width
        height = position.height

        posX = position.x
        posY = position.y

        centerX = position.centerX
        centerY = position.centerY
    }

    abstract override fun renderBelow(alphaMult: Float)

    abstract override fun render(alphaMult: Float)

    final fun createGLLines(color: Color, alphaMult: Float, function: () -> Unit)
    {
        GL11.glPushMatrix()

        GL11.glTranslatef(0f, 0f, 0f)
        GL11.glRotatef(0f, 0f, 0f, 1f)

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * alphaMult)

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINE_STRIP)

        function()

        GL11.glEnd()
        GL11.glPopMatrix()
    }

    final fun createGLRectangle(color: Color, alphaMult: Float, function: () -> Unit)
    {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f)

        function()

        GL11.glPopMatrix()
    }

    override fun advance(amount: Float)
    {

    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        for (event in events)
        {
            if (event.isMouseEvent)
            {
                if (event.x.toFloat() in posX..(posX + width) && event.y.toFloat() in posY..(posY +height))
                {
                    for (onHover in onHoverFunctions)
                    {
                        isHovering = true
                        onHover(event)
                    }
                }
                else
                {
                    for (onNotHover in onNotHoverFunctions)
                    {
                        isHovering = false
                        onNotHover(event)
                    }
                }
            }

            if (event.isMouseDownEvent && position != null)
            {
                if (event.x.toFloat() in posX..(posX + width) && event.y.toFloat() in posY..(posY +height))
                {
                    //sliderPositionX = event.x.toFloat() - position!!.centerX
                    isHeld = true
                    for (onClick in onClickFunctions)
                    {
                        onClick(event)
                    }
                    event.consume()
                }
                else
                {
                    for (onClickOutside in onClickOutsideFunctions)
                    {
                        onClickOutside(event)
                    }
                }
            }
            else if (event.isMouseUpEvent)
            {
                for (onNotHeld in onNotHeldFunctions)
                {
                    onNotHeld(event)
                }
                isHeld = false
            }
            else if (event.isMouseEvent && isHeld)
            {
                //sliderPositionX = event.x.toFloat() - position!!.centerX
                for (onClick in onHeldFunctions)
                {
                    onClick(event)
                }
                event.consume()
            }
        }

        for (onUpdate in onUpdateFunction)
        {
            onUpdate(events)
        }
    }
}