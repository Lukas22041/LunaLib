package lunalib.backend.ui.components

import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import java.awt.Color

class LunaUIColorPicker <T> (var value: Color, var hasParagraph: Boolean, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {


    var hueSlider: LunaUISlider<Float>? = null
    var satSlider: LunaUISlider<Float>? = null
    var brightSlider: LunaUISlider<Float>? = null


    override fun positionChanged(position: PositionAPI) {

        if (hueSlider == null) {
            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)

            hueSlider = LunaUISlider(1f,false, minValue, maxValue, width, height * 0.75f,"", group, panel, pan!!)

            hueSlider!!.lunaElement!!.position.inTL(0f, 0f)
            hueSlider!!.onHeld {
                try {
                    paragraph!!.text = slider!!.value.toString()
                }
                catch (e: Throwable) { }
            }
            Color.getHSBColor()
        }

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }
}