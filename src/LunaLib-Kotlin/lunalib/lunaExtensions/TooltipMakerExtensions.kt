package lunalib.lunaExtensions

import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaExtensions.TooltipMakerExtensions.addLunaElement
import lunalib.lunaUI.LunaElement
import lunalib.lunaUI.LunaToggleButton

object TooltipMakerExtensions {


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

    /**
     * Example Class that extends LunaElement.
     *
     * Simply Switches between a true/false value and switches the buttons color & text between "Enabled" and "Disabled".
     */
    fun TooltipMakerAPI.addLunaToggleButton(defaultValue: Boolean, width: Float, height: Float) : LunaToggleButton
    {
        var element = LunaToggleButton(defaultValue, this, width, height)
        return element
    }


    /** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)
     *
     * Creates a Paragraph that automaticly highlights its content based on syntax.
     * */
    /*fun TooltipMakerAPI.addAutoPara(text: String, variables: Map<String, Any>? = null, extraColors: Map<String, Color>? = null)
    {

    }*/
}