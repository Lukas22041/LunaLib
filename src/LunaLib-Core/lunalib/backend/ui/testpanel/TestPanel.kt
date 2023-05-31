package lunalib.backend.ui.testpanel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.*
import lunalib.lunaUI.elements.LunaSpriteElement
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin
import org.lwjgl.input.Keyboard
import java.awt.Color

class TestPanel : LunaBaseCustomPanelPlugin() {

    var width = 0f
    var height = 0f

    override fun init() {

        enableCloseButton = true
        width = panel.position.width
        height = panel.position.height

        var element = panel.createUIElement(width, height, false)
        panel.addUIElement(element)



        var lunaElement = element.addLunaElement(width - 40, height - 40)
        lunaElement.enableTransparency = true
        lunaElement.position.inTL(20f, 20f)

        var newElement = lunaElement.elementPanel.createUIElement(width - 40, height - 40, true)


        newElement.addPara("All Elements here can simply be added through TooltipMakerAPI extensions in Kotlin. You can however also just instantiate them in Java, but many features are kotlin exclusive. ", 0f).position.inTL(10f, 10f)

        newElement.addSpacer(10f)
        newElement.addPara("All UI Elements in Lunalib extend the LunaElement class, which is a simple UI Element that by default does nothing, but can be set to render its border, background" +
                " and has lambdas that allow to add functionality on hover, click, etc. It also holds its own TooltipMakerAPI, allowing you to add other UI elements within itself.", 0f)

        newElement.addSpacer(10f)
        newElement.addLunaElement(200f, 100f)
        newElement.addSpacer(30f)

        newElement.addPara("The LunaToggleButton is a simple button that changes between true and false, the text displayed for each state can be changed.", 0f)
        newElement.addSpacer(10f)
        newElement.addLunaToggleButton(true,  100f, 30f)
        newElement.addSpacer(30f)

        newElement.addPara("The LunaChargeButton requires the user to hold leftclick to trigger its activation.", 0f)
        newElement.addSpacer(10f)
        newElement.addLunaChargeButton(150f, 50f)
        newElement.addSpacer(30f)

        newElement.addPara("The LunaColorPicker is a simple element that allows the user to click somewhere along its width to pick a color.", 0f)
        newElement.addSpacer(10f)
        var picker  = newElement.addLunaColorPicker(0.6f, 200f, 20f)
        newElement.addSpacer(30f)
        newElement.addPara("The LunaProgressBar simply fills from left to right depending on the state of its value and the min/max value that has been set. " +
                "\nFor Demonstration, this one is set to increase on leftclick and its color is linked to aboves color picker.", 0f)
        newElement.addSpacer(10f)
        newElement.addLunaProgressBar(75f, 0f, 100f, 300f, 50f, Misc.getBasePlayerColor()).apply {

            advance {
                borderColor = picker.getColor()
                backgroundColor = picker.getColor()
            }

            showNumber(false)

            onHeld {
                if (getValue() >= 100f)
                {
                    changeValue(0f)
                }
                changeValue(getValue() + 1f)
            }
        }
        newElement.addSpacer(30f)

        newElement.addPara("The LunaTextfield is a recreation of the vanilla text field that allows multiline text.", 0f)
        newElement.addSpacer(10f)
        newElement.addLunaTextfield("Test", true,200f, 100f)
        newElement.addSpacer(30f)

        newElement.addPara("The LunaSpriteElement holds and renders an assigned sprite. The Sprite can either be stretched to the elements size, or the element can be stretched to the sprites size." +
                " If it stretches the elements size, it can also be set to scale up the size of the sprite to a minimum/maximum while keeping the same aspect ratio.", 0f)
        newElement.addSpacer(10f)
        newElement.addLunaSpriteElement(Global.getSettings().allShipHullSpecs.random().spriteName,LunaSpriteElement.ScalingTypes.STRETCH_ELEMENT,200f,200f).apply {
            enforceSize(50f, 100f, 50f, 100f)
        }
        newElement.addSpacer(30f)

        lunaElement.elementPanel.addUIElement(newElement)


    }

    override fun processInput(events: MutableList<InputEventAPI>) {
        super.processInput(events)

        events.forEach {
            if (it.isKeyDownEvent && it.eventValue == Keyboard.KEY_ESCAPE)
            {
                close()
            }
        }
    }
}