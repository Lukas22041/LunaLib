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
import org.lwjgl.util.vector.Vector2f

internal class LunaUIKeybindButton(var keycode: Int?, var regularButton: Boolean, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    var button: LunaUIButton? = null

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)


        if (keycode != null && button == null && lunaElement != null)
        {
            var pan = lunaElement!!.createUIElement(width, height, false)
            uiElement.addComponent(pan)
            lunaElement!!.addUIElement(pan)
            pan.position.inTL(0f, 0f)

            button = LunaUIButton(false, false, width, height,"Test", group, panel!!, pan!!)
            button!!.buttonText!!.text = "Key: ${Keyboard.getKeyName(keycode!!)}"
            if (keycode == 0)
            {
                button!!.buttonText!!.text = "Key: None"
            }
            button!!.borderAlpha = 0.5f
            button!!.position!!.inTL(0f,0f)
            button!!.onClick {
                setSelected()
            }
            button!!.onClickOutside {
                unselect()
            }
            button!!.onUpdate {
                if (isSelected())
                {
                    button!!.borderColor = Misc.getDarkPlayerColor().brighter()
                }
                else
                {
                    button!!.borderColor = Misc.getDarkPlayerColor()
                }
                if (keycode == 0)
                {
                    button!!.backgroundAlpha = 0.25f
                }
                else
                {
                    button!!.backgroundAlpha = 1f
                }
            }

            button!!.onHover {
                button!!.backgroundColor = Misc.getDarkPlayerColor().brighter()
            }
            button!!.onNotHover {
                button!!.backgroundColor = Misc.getDarkPlayerColor()
            }

        }

        if (button != null)
        {
            button!!.buttonText!!.position.inTL(width / 2 - button!!.buttonText!!.computeTextWidth(button!!.buttonText!!.text) / 2, height / 2 - button!!.buttonText!!.computeTextHeight(button!!.buttonText!!.text) / 2)
        }
    }

    override fun renderBelow(alphaMult: Float) {
    }

    override fun render(alphaMult: Float) {
    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        if (button != null)
        {
            for (event in events)
            {
                if (event.isKeyDownEvent && button!!.isSelected() && event.eventValue == Keyboard.KEY_ESCAPE)
                {
                    keycode = 0
                    button!!.buttonText!!.text = "Key: None"
                    button!!.unselect()
                    button!!.buttonText!!.position.inTL(width / 2 - button!!.buttonText!!.computeTextWidth(button!!.buttonText!!.text) / 2, height / 2 - button!!.buttonText!!.computeTextHeight(button!!.buttonText!!.text) / 2)
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                    event.consume()
                    continue
                }
                if (button!!.isSelected() && event.isKeyDownEvent && event.eventValue != -1 && event.eventValue != Keyboard.KEY_LSHIFT && event.eventValue != Keyboard.KEY_LCONTROL && event.eventValue != Keyboard.KEY_LMENU)
                {
                    keycode = event.eventValue
                    button!!.unselect()

                    if (event.eventValue == Keyboard.KEY_ESCAPE) keycode = 0
                    button!!.buttonText!!.text = "Key: ${Keyboard.getKeyName(keycode!!)}"
                    button!!.buttonText!!.position.inTL(width / 2 - button!!.buttonText!!.computeTextWidth(button!!.buttonText!!.text) / 2, height / 2 - button!!.buttonText!!.computeTextHeight(button!!.buttonText!!.text) / 2)
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)

                    event.consume()
                    continue
                }
            }
        }
    }
}