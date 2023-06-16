package lunalib.backend.ui.components

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.components.base.LunaUIButton
import org.lwjgl.input.Keyboard

internal class LunaUIRadioButton(var value: String?, var choices: List<String>?, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var created = false
    var buttonHeight = 25f
    var buttons: MutableList<LunaUIButton> = ArrayList()

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)



        if (choices != null && !created && lunaElement != null)
        {
            var pan = lunaElement!!.createUIElement(width, height, false)
           // uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)
            var element = LunaUIButton(false, false, 0f, 0f,"", group, panel!!, pan!!)
            for (choice in choices!!)
            {

                var button = LunaUIButton(false, false, width, buttonHeight,"", group, panel!!, pan!!)
                buttons.add(button)
                if (choice == value)
                {
                    button.setSelected()
                    backgroundAlpha = 0.75f
                }
                button!!.buttonText!!.text = "$choice"
                button!!.buttonText!!.position.inTL(width / 2 - button!!.buttonText!!.computeTextWidth(button!!.buttonText!!.text) / 2, buttonHeight / 2 - button!!.buttonText!!.computeTextHeight(button!!.buttonText!!.text) / 2)

                if (!created)
                {
                    button.position!!.inTL(0f, 0f)
                    created = true
                }
                else
                {
                    height += button.height + 5
                }

                button!!.borderAlpha = 0.5f
                button!!.backgroundAlpha = 0.5f
                //button!!.position!!.inTL(0f,0f)
                button!!.onClick {
                    setSelected()
                    value = button.buttonText!!.text
                }
                button!!.onUpdate {
                    if (isSelected())
                    {
                        button!!.borderColor = Misc.getDarkPlayerColor().brighter()
                        button!!.backgroundColor = Misc.getDarkPlayerColor().brighter()

                        if (button.isHovering)
                        {
                            backgroundAlpha = 0.75f
                        }
                        else
                        {
                            backgroundAlpha = 0.65f
                        }
                    }
                    else
                    {
                        button!!.borderColor = Misc.getDarkPlayerColor()
                        button!!.backgroundColor = Misc.getDarkPlayerColor()

                        if (button.isHovering)
                        {
                            backgroundAlpha = 0.7f
                        }
                        else
                        {
                            backgroundAlpha = 0.4f
                        }
                    }
                }

                button!!.onHover {
                    if (button.isSelected())
                    {
                        backgroundAlpha = 0.75f
                    }
                    else
                    {
                        backgroundAlpha = 0.5f
                    }

                }
                button!!.onNotHover {
                    button!!.backgroundColor = Misc.getDarkPlayerColor()
                }

                pan.addSpacer(5f)
                //height += button.height + 5
            }


        }
    }

    override fun renderBelow(alphaMult: Float) {
    }

    override fun render(alphaMult: Float) {
    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

    }
}