package lunalib.backend.ui.components.base

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI


class LunaUISprite(var spritePath: String, var maxX: Float, var maxY: Float, minX: Float, minY: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var sprite = Global.getSettings().getSprite(spritePath)
    var textureWidth = 0f
    var textureHeight = 0f

    private var overwrite = false
    private var overwriteMod = 1f

    init {
        textureWidth = sprite.width ?: 0f
        textureHeight = sprite.height ?: 0f

        if (textureWidth > maxX)
        {
            textureWidth = maxX
        }
        if (textureHeight > maxY)
        {
            textureHeight = maxY
        }

        if (textureWidth < minX)
        {
            textureWidth = minY
        }
        if (textureHeight < minY)
        {
            textureHeight = minY
        }
        sprite.setSize(textureWidth, textureHeight)
    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)
        if (this.position != null)
        {
            if (sprite != null)
            {
                sprite.setSize(textureWidth, textureHeight)
                position.setSize(textureWidth, textureHeight)
            }
        }
    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {
        if (position != null && sprite != null)
        {


            sprite.alphaMult = 1f
            sprite.render(posX, posY)
            sprite.setSize(textureWidth, textureHeight)
            position!!.setSize(textureWidth, textureHeight)
        }
    }
}