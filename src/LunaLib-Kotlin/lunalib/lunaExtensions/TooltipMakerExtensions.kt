package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.*
import lunalib.lunaUI.panel.LunaWindowPlugin
import java.awt.Color


/**
 * Creates a LunaElement. LunaElements are the basic implementation of Lunalib's custom UI components.
 *
 * They do not have any functionality by default, but its methods like **onClick** or **onHover** can be used to add behaviours to it.
 *
 * Otherwise they work exactly the same as Vanilla UI Elements, but additionaly you can also access some of their underlying methods, like the **render** method through a lambda.
 */
fun TooltipMakerAPI.addLunaElement(width: Float, height: Float) : LunaElement
{
    var element = LunaElement(this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaToggleButton(defaultValue: Boolean, width: Float, height: Float) : LunaToggleButton
{
    var element = LunaToggleButton(defaultValue, this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaChargeButton(width: Float, height: Float) : LunaChargeButton
{
    var element = LunaChargeButton(this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaTextfield(text: String, multiline: Boolean, width: Float, height: Float) : LunaTextfield
{
    var element = LunaTextfield(text, multiline, Misc.getBasePlayerColor(),this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaSpriteElement(spritePath: String, scaling: LunaSpriteElement.ScalingTypes, width: Float, height: Float) : LunaSpriteElement
{
    var element = LunaSpriteElement(spritePath, scaling,this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaColorPicker(hue: Float, width: Float, height: Float) : LunaColorPicker
{
    var element = LunaColorPicker(hue, this, width, height)
    return element
}

fun TooltipMakerAPI.addLunaProgressBar(defaultValue: Float, min: Float, max: Float, width: Float, height: Float, textColor: Color) : LunaProgressBar
{
    var element = LunaProgressBar(defaultValue, min, max, textColor, this, width, height)
    return element
}

/*
fun TooltipMakerAPI.addWindow(to: UIPanelAPI, width: Float, height: Float, lambda: (LunaWindowPlugin) -> Unit) {
    var parentPanel = Global.getSettings().createCustom(width, height, null)
    this.addCustom(parentPanel, 0f)

    var plugin = LunaWindowPlugin(parentPanel, this)
    var panel = parentPanel.createCustomPanel(width, height, plugin)
    plugin.position = panel.position
    plugin.panel = panel
    parentPanel.addComponent(panel)

    parentPanel.position.rightOfTop(to, 20f)

    lambda(plugin)
}
*/


