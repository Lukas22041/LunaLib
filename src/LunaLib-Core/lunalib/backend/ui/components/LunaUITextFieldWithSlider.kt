package lunalib.backend.ui.components

import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUISlider
import lunalib.backend.ui.components.base.LunaUITextField

class LunaUITextFieldWithSlider <T : Number> (var value: T?, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var textField: LunaUITextField<T>? = null
    var valueSlider: LunaUISlider<T>? = null


    init {

    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (textField == null && lunaElement != null && value != null) {

            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)

            textField = LunaUITextField(value as T,minValue, maxValue, 200f, 30f,"Test", "TestGroup", panel, pan!!)
            textField!!.position!!.inTL(0f, 0f)
            textField!!.onUpdate {
                if (textField!!.isSelected())
                {
                    if (value is Double)
                    {
                        try {
                            var text = textField!!.paragraph!!.text.replace("_", "")
                            if (text != "")
                            {
                                var curValue = text.toDouble() as T

                                valueSlider!!.value = curValue

                                var min = centerX - width / 2 + width / 20
                                var max = centerX + width / 2 - width / 20

                                var level = (curValue as Double - minValue) / (maxValue - minValue)
                                level -= 0.5f
                                var scale = max - min

                                valueSlider!!.sliderPosX = ((scale  * level).toFloat())
                            }
                        } catch (e: Throwable) {}
                    }
                    if (value is Float)
                    {
                        try {
                            var text = textField!!.paragraph!!.text.replace("_", "")
                            if (text != "")
                            {
                                var curValue = text.toFloat() as T

                                valueSlider!!.value = curValue

                                var min = centerX - width / 2 + width / 20
                                var max = centerX + width / 2 - width / 20

                                var level = (curValue as Float - minValue) / (maxValue - minValue)
                                level -= 0.5f
                                var scale = max - min

                                valueSlider!!.sliderPosX = ((scale  * level).toFloat())
                            }
                        } catch (e: Throwable) {}
                    }
                    if (value is Int)
                    {
                        try {
                            var text = textField!!.paragraph!!.text.replace("_", "")
                            if (text != "")
                            {
                                var curValue = text.toInt() as T

                                valueSlider!!.value = curValue

                                var min = centerX - width / 2 + width / 20
                                var max = centerX + width / 2 - width / 20

                                var level = (curValue as Int - minValue) / (maxValue - minValue)
                                level -= 0.5f
                                var scale = max - min

                                valueSlider!!.sliderPosX = ((scale  * level).toFloat())



                            }
                        } catch (e: Throwable) {}
                    }
                }
            }

            valueSlider = LunaUISlider(value as T, minValue, maxValue, width, height * 0.65f,"", group, panel, pan!!)
            valueSlider!!.onHeld {
                try {
                    textField!!.paragraph!!.text = valueSlider!!.value.toString()
                }
                catch (e: Throwable) { }
            }
        }
    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }
}