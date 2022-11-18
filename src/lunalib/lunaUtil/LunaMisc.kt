package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.util.Misc
import java.awt.Color
import kotlin.random.Random

/**
A class containing a collection of different Utilities that did not require their own class.

[View Github Page](https://github.com/Lukas22041/LunaLib/wiki/LunaMisc)
*/
object LunaMisc
{
    /**
     * Generates and returns a random color.
     * @param saturation Saturation of the color.
     */
    @JvmStatic
    fun randomColor(saturation: Float): Color {
        val hue = Random.nextFloat()
        val luminance = 0.9f
        return Color.getHSBColor(hue, saturation, luminance)
    }

    /**
     * Generates and returns a random color.
     * @param saturation Saturation of the color.
     * @param alpha Alpha of the generated color.
     */
    @JvmStatic
    fun randomColor(saturation: Float, alpha: Int) : Color {
        val hue = Random.nextFloat()
        val luminance = 0.9f
        val color = Color.getHSBColor(hue, saturation, luminance)
        return Color(color.red, color.green, color.blue, alpha)
    }

    /**
     *Creates an intel Message (The pop-ups in the bottom left of the UI.
     * @param Title
     * @param Text
     */
    @JvmStatic
    fun createIntelMessage(Title: String, Text: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

    /**
     *Creates an intel Message (The pop-ups in the bottom left of the UI.
     * @param Title
     * @param Text
     * @param Icon the Spritename of an icon that should be used.
     */
    @JvmStatic
    fun createIntelMessage(Title: String, Text: String, Icon: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.icon = Icon
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

}