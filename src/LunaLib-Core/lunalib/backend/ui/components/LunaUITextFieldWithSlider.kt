package lunalib.backend.ui.components

import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.sun.org.apache.bcel.internal.generic.GOTO
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUISlider
import lunalib.backend.ui.components.base.LunaUITextField

internal class LunaUITextFieldWithSlider <T : Number> (var value: T?, var minValue: Float, var maxValue: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var textField: LunaUITextField<T>? = null
    var valueSlider: LunaUISlider<T>? = null


    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (textField == null && lunaElement != null && value != null) {

            var pan = lunaElement!!.createUIElement(width, height, false)
            //uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)

            textField = LunaUITextField(value as T, minValue, maxValue, width, height * 0.6f,"Test", group, panel, pan!!)
            textField!!.position!!.inTL(0f, 0f)
            textField!!.borderAlpha = 0.5f

            textField!!.onUpdate {
                if (textField!!.isSelected())
                {
                    if (value is Double)
                    {
                        try {
                            //var text = textField!!.paragraph!!.text.replace("_", "")
                            var text = textField!!.paragraph!!.text
                            if (text != "")
                            {
                                var curValue = text.toDouble() as T
                                value = curValue
                                valueSlider!!.value = curValue

                                valueSlider!!.setSliderPositionByValue(curValue)
                            }
                        } catch (e: Throwable) {}
                    }
                    if (value is Float)
                    {
                        try {
                            //var text = textField!!.paragraph!!.text.replace("_", "")
                            var text = textField!!.paragraph!!.text
                            if (text != "")
                            {
                                var curValue = text.toFloat() as T
                                value = curValue
                                valueSlider!!.value = curValue

                                valueSlider!!.setSliderPositionByValue(curValue)
                            }
                        } catch (e: Throwable) {}
                    }
                    if (value is Int)
                    {
                        try {
                            //var text = textField!!.paragraph!!.text.replace("_", "")
                            var text = textField!!.paragraph!!.text
                            if (text != "")
                            {
                                var curValue = text.toInt() as T
                                value = curValue
                                valueSlider!!.value = curValue

                                valueSlider!!.setSliderPositionByValue(curValue)
                            }
                        } catch (e: Throwable) {}
                    }
                }
            }
            pan.addSpacer(2f)

            valueSlider = LunaUISlider(value as T, minValue, maxValue, width, height * 0.4f,"", group, panel, pan!!)
            valueSlider!!.borderAlpha = 0.5f
            valueSlider!!.onHeld {
                try {
                    textField!!.paragraph!!.text = valueSlider!!.value.toString()
                    value = valueSlider!!.value
                }
                catch (e: Throwable) { }
            }
        }
    }

    fun updateValue(newValue: Any)
    {
        value = newValue as T
        textField!!.paragraph!!.text = value.toString()
        valueSlider!!.setSliderPositionByValue(value!!)
    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (textField != null)
        {
            this.value = textField!!.value
        }
    }
}