package lunalib.lunaUI

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import java.awt.Color

class LunaToggleButton(var value: Boolean, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    init {
        if (value) addText("Enabled", baseColor = Misc.getBasePlayerColor())
        else addText("Disabled", baseColor = Misc.getBasePlayerColor())
        centerText()

        onHoverEnter {
            playScrollSound()
            borderColor = Misc.getDarkPlayerColor().brighter()
        }
        onHoverExit {
            borderColor = Misc.getDarkPlayerColor()
        }

        onClick {
            value = !value
            playClickSound()

            if (value)
            {
                changeText("Enabled")
                backgroundColor = Misc.getDarkPlayerColor().darker()
            }
            else
            {
                changeText("Disabled")
                backgroundColor = Misc.getDarkPlayerColor().darker().darker()
            }
            centerText()
        }
    }
}