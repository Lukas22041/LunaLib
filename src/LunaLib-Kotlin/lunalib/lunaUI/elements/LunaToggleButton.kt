package lunalib.lunaUI.elements

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

class LunaToggleButton(var value: Boolean, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    private var enabledText = "Enabled"
    private var disabledText = "Disabled"

    init {
        if (value) addText(enabledText, baseColor = Misc.getBasePlayerColor())
        else addText(disabledText, baseColor = Misc.getBasePlayerColor())
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
                changeText(enabledText)
                backgroundColor = Misc.getDarkPlayerColor().darker()
            }
            else
            {
                changeText(disabledText)
                backgroundColor = Misc.getDarkPlayerColor().darker().darker()
            }
            centerText()
        }
    }

    fun changeStateText(enabled: String, disabled: String)
    {
        enabledText = enabled
        disabledText = disabled
        if (value)
        {
            changeText(enabledText)
            backgroundColor = Misc.getDarkPlayerColor().darker()
        }
        else
        {
            changeText(disabledText)
            backgroundColor = Misc.getDarkPlayerColor().darker().darker()
        }
    }
}