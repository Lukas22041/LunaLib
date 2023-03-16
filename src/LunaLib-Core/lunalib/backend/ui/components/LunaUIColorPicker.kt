package lunalib.backend.ui.components

import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUISlider
import lunalib.backend.ui.components.base.LunaUITextField
import lunalib.backend.util.getLunaString
import org.lwjgl.opengl.GL11
import java.awt.Color

internal class LunaUIColorPicker (var value: Color?, var hasParagraph: Boolean, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var textField: LunaUITextField<String>? = null

    var hueSlider: LunaUISlider<Float>? = null
    var satSlider: LunaUISlider<Float>? = null
    var brightSlider: LunaUISlider<Float>? = null

    var huePara: LabelAPI? = null
    var satPara: LabelAPI? = null
    var brightPara: LabelAPI? = null

    var h = 1f
    var s = 1f
    var b = 1f

    init {

    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)

        if (hueSlider == null && lunaElement != null && value != null) {

            h = Color.RGBtoHSB(value!!.red, value!!.green, value!!.blue, null).get(0)
            s = Color.RGBtoHSB(value!!.red, value!!.green, value!!.blue, null).get(1)
            b = Color.RGBtoHSB(value!!.red, value!!.green, value!!.blue, null).get(2)

            var pan = lunaElement!!.createUIElement(width, height, false)
            //uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)
            textField = LunaUITextField("",0f, 1f, width, height * 0.3f,"Test", group, panel, pan!!)
            textField!!.lunaElement!!.position.inTL(0f, 0f)
            textField!!.value = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
            textField!!.borderAlpha = 0.5f

            pan.addSpacer(4f)

            hueSlider = LunaUISlider(h, 0f, 1f, width, height * 0.23f,"", group, panel, pan!!)
            hueSlider!!.borderAlpha = 0.5f
            hueSlider!!.onHeld {
                try {
                    h = hueSlider!!.value
                    value = Color.getHSBColor(h, s, b)
                    textField!!.value = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                    textField!!.paragraph!!.text = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                }
                catch (e: Throwable) { }
            }

            pan.addSpacer(4f)


            satSlider = LunaUISlider(s, 0f, 1f, width, height * 0.23f,"", group, panel, pan!!)
            satSlider!!.borderAlpha = 0.5f
            satSlider!!.onHeld {
                try {
                    s = satSlider!!.value
                    value = Color.getHSBColor(h, s, b)
                    textField!!.value = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                    textField!!.paragraph!!.text = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                }
                catch (e: Throwable) { }
            }

            pan.addSpacer(4f)

            brightSlider = LunaUISlider(b, 0f, 1f, width, height * 0.23f,"", group, panel, pan!!)
            brightSlider!!.borderAlpha = 0.5f
            brightSlider!!.onHeld {
                try {
                    b = brightSlider!!.value
                    value = Color.getHSBColor(h, s, b)
                    textField!!.value = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                    textField!!.paragraph!!.text = String.format("#%02x%02x%02x", value!!.red, value!!.green, value!!.blue);
                }
                catch (e: Throwable) { }
            }


            textField!!.onUpdate {
                if (textField!!.isSelected())
                {
                    try {
                        var color = Color.decode(textField!!.paragraph!!.text.replace("_", ""))
                        hueSlider!!.value = Color.RGBtoHSB(color.red, color.green, color.blue, null).get(0)
                        satSlider!!.value = Color.RGBtoHSB(color.red, color.green, color.blue, null).get(1)
                        brightSlider!!.value = Color.RGBtoHSB(color.red, color.green, color.blue, null).get(2)

                        var list = listOf(hueSlider, satSlider, brightSlider)
                        for (entry in list.indices)
                        {
                            var min = centerX - width / 2 + width / 20
                            var max = centerX + width / 2 - width / 20

                            var level = (Color.RGBtoHSB(color.red, color.green, color.blue, null).get(entry) as Float - 0f) / (1f - 0f)
                            level -= 0.5f
                            var scale = max - min

                            list.get(entry)!!.sliderPosX = ((scale  * level).toFloat())
                        }
                        h = hueSlider!!.value
                        s = satSlider!!.value
                        b = brightSlider!!.value
                        value = Color.getHSBColor(h, s, b)
                    } catch (e: Throwable) {}
                }
            }

            huePara = pan.addPara("colorSelectorHue".getLunaString(), 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            huePara!!.position.inTL((width / 2 - huePara!!.computeTextWidth(huePara!!.text) / 2) , height * 0.31f + 4)

            satPara = pan.addPara("colorSelectorSaturation".getLunaString(), 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            satPara!!.position.inTL((width / 2 - satPara!!.computeTextWidth(satPara!!.text) / 2) , height * 0.31f + height * 0.23f + 8)

            brightPara = pan.addPara("colorSelectorBrightness".getLunaString(), 1f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            brightPara!!.position.inTL((width / 2 - brightPara!!.computeTextWidth(brightPara!!.text) / 2) , height * 0.31f + height * 0.46f + 12)

            // for the extra gaps between sliders
            panel.position.setSize(panel.position.width, panel.position.height + 12)
            uiElement.addSpacer(12f)
        }
    }

    override fun renderBelow(alphaMult: Float) {

        createGLRectangle(value!!, alphaMult) {
            GL11.glRectf(posX + width + 5, posY - 12, posX + width + width / 8, posY + height)
        }
    }

    fun updateValue(newValue: Color, hex: String)
    {

        h = Color.RGBtoHSB(newValue!!.red, newValue!!.green, newValue!!.blue, null).get(0)
        s = Color.RGBtoHSB(newValue!!.red, newValue!!.green, newValue!!.blue, null).get(1)
        b = Color.RGBtoHSB(newValue!!.red, newValue!!.green, newValue!!.blue, null).get(2)

        value = Color.getHSBColor(h, s, b)


        textField!!.paragraph!!.text = hex
        hueSlider!!.setSliderPositionByValue(h)
        satSlider!!.setSliderPositionByValue(s)
        brightSlider!!.setSliderPositionByValue(b)
    }

    override fun render(alphaMult: Float) {

    }
}