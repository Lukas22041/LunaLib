package lunalib.backend.ui.components.base

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.WeakHashMap

abstract class LunaUIBaseElement(var width: Float = 0f, var height: Float = 0f, var key: Any, var group: String, var panel: CustomPanelAPI, var uiElement: TooltipMakerAPI) : CustomUIPanelPlugin
{
    companion object {
        //Needs a re-do at some point, causes a memory leak if its not cleared manually after closing the menu.
        var selectedMap: MutableMap<String, LunaUIBaseElement?> = WeakHashMap()
    }

    var lunaElement: CustomPanelAPI? = null

    private var onClickFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onClickOutsideFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onHeldFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onNotHeldFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onHoverFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onHoverEnterFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()
    private var onNotHoverFunctions: MutableList<LunaUIBaseElement.(InputEventAPI) -> Unit> = ArrayList()

    private var onUpdateFunction: MutableList<LunaUIBaseElement.(List<InputEventAPI>) -> Unit> = ArrayList()
    private var onSelectFunction: MutableList<LunaUIBaseElement.() -> Unit> = ArrayList()

    var position: PositionAPI? = null

    var posX: Float = 0f
        private set
    var posY: Float = 0f
        private set

    var centerX: Float = 0f
        private set
    var centerY: Float = 0f
        private set

    var borderAlpha = 1f
    var backgroundAlpha = 1f

    var baseColor = Misc.getBasePlayerColor()
    var brightColor = Misc.getBrightPlayerColor()
    var darkColor = Misc.getDarkPlayerColor()

    var isHeld = false
    var isHovering = false

    init {

        //Should be "lunaElement = Global.getSettings().createCustom(width, height, this)" instead, as that removes the requirement for the panel parameter.
        lunaElement = panel.createCustomPanel(width, height, this)
        uiElement.addCustom(lunaElement, 0f)
    }

    fun isSelected() : Boolean
    {
        if (selectedMap.get(group) == null) return false
        if (selectedMap.get(group) == this) return true
        return false
    }

    fun onClick(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onClickFunctions.add(function)
    }
    fun onClickOutside(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onClickOutsideFunctions.add(function)
    }

    fun onHeld(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onHeldFunctions.add(function)
    }
    fun onNotHeld(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onNotHeldFunctions.add(function)
    }


    fun onHover(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onHoverFunctions.add(function)
    }
    fun onHoverEnter(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onHoverEnterFunctions.add(function)
    }
    fun onNotHover(function: LunaUIBaseElement.(InputEventAPI) -> Unit)
    {
        onNotHoverFunctions.add(function)
    }

    fun onUpdate(function: LunaUIBaseElement.(List<InputEventAPI>) -> Unit)
    {
        onUpdateFunction.add(function)
    }

    fun onSelect(function: LunaUIBaseElement.() -> Unit)
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
        if (selectedMap.get(group) == this)
        {
            selectedMap.set(group, null)
        }
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
            color.alpha / 255f * (alphaMult * borderAlpha))

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
        GL11.glDisable(GL11.GL_CULL_FACE)
       /* GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_FRONT)
        GL11.glFrontFace(GL11.GL_CW)*/
        GL11.glEnable(GL11.GL_BLEND)
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * (alphaMult * backgroundAlpha))

        function()

        GL11.glPopMatrix()
    }

    override fun advance(amount: Float)
    {

    }

    var hoverEnter = false

    override fun processInput(events: MutableList<InputEventAPI>) {
        for (event in events)
        {
            if (event.isMouseEvent)
            {
                if (event.x.toFloat() in posX..(posX + width) && event.y.toFloat() in posY..(posY +height))
                {
                    if (hoverEnter == false)
                    {
                        hoverEnter = true
                        for (onHoverEnter in onHoverEnterFunctions)
                        {
                            onHoverEnter(event)
                        }
                    }
                    for (onHover in onHoverFunctions)
                    {
                        isHovering = true
                        onHover(event)
                    }
                }
                else
                {
                    hoverEnter = false
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
                for (onHeld in onHeldFunctions)
                {
                    onHeld(event)
                }
                event.consume()
            }
        }

        for (onUpdate in onUpdateFunction)
        {
            onUpdate(events)
        }
    }

    final override fun buttonPressed(buttonId: Any?) {

    }
}