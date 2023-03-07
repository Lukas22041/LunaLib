package lunalib.backend.ui.components.base

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.opengl.GL11


class LunaUIPlaceholder(var renderBackground: Boolean,width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var addedBackgroundImage: Boolean = false

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        /*if (!addedBackgroundImage && lunaElement != null)
        {
            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(-5f, 0f)

            addedBackgroundImage = true
        }*/
    }

    override fun renderBelow(alphaMult: Float) {
        if (renderBackground)
        {
            createGLRectangle(darkColor, alphaMult * 0.5f) {
                GL11.glRectf(posX, posY , posX + width, posY + height)
            }
        }
    }

    override fun render(alphaMult: Float) {
        if (renderBackground)
        {
            createGLLines(darkColor, alphaMult * 0.5f)
            {
                GL11.glVertex2f(posX, posY)
                GL11.glVertex2f(posX, posY + height)
                GL11.glVertex2f(posX + width, posY + height)
                GL11.glVertex2f(posX + width, posY)
                GL11.glVertex2f(posX, posY)
            }
        }
    }
}