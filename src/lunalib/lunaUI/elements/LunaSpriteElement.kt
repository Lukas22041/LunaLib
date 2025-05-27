package lunalib.lunaUI.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import java.awt.Color

class LunaSpriteElement(var spritePath: String, var scaling: ScalingTypes = ScalingTypes.STRETCH_SPRITE, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    enum class ScalingTypes {
        STRETCH_SPRITE, STRETCH_ELEMENT, NONE
    }


    private var sprite: SpriteAPI

    init {
        renderBackground = false
        renderBorder = false

        Global.getSettings().loadTexture(spritePath)
        sprite = Global.getSettings().getSprite(spritePath)

        when (scaling)
        {
            ScalingTypes.STRETCH_SPRITE -> {
                sprite.setSize(width, height)
            }
            ScalingTypes.STRETCH_ELEMENT -> {
                position.setSize(sprite.width, sprite.height)
            }
            else -> {}
        }
    }

    /**
     * Stretch Element Only. Scales a sprite until either one of its width/height reaches the minimum size.
    */
    fun enforceSize(minWidth: Float, maxWidth: Float, minHeight: Float, maxHeight: Float)
    {
        if (scaling == ScalingTypes.STRETCH_ELEMENT)
        {
            while (sprite.width < minWidth && sprite.height < minHeight)
            {
                sprite.setSize(sprite.width + sprite.width / 100, sprite.height + sprite.height / 100)
                position.setSize(sprite.width, sprite.height)
            }

            while (sprite.width > maxWidth && sprite.height > maxHeight)
            {
                sprite.setSize(sprite.width - sprite.width / 100, sprite.height  - sprite.height / 100)
                position.setSize(sprite.width, sprite.height)
            }
        }
    }

    fun getSprite() = sprite

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

        var ogAlpha = sprite.alphaMult

        sprite.alphaMult = ogAlpha * alphaMult
        sprite.render(x, y)

        sprite.alphaMult = ogAlpha

    }

}