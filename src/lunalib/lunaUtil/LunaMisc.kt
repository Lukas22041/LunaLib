package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel
import com.fs.starfarer.api.util.Misc
import java.awt.Color
import kotlin.random.Random

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
    fun randomColor(saturation: Float, alpha: Int) : Color
    {
        val hue = Random.nextFloat()
        val luminance = 0.9f
        val color = Color.getHSBColor(hue, saturation, luminance)
        return Color(color.red, color.green, color.blue, alpha)
    }

    /**
     *Creates an intel popup
     * @param Title
     * @param Text
     */
    @JvmStatic
    fun createIntelPopup(Title: String, Text: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

     /**
     *Creates an intel popup
     * @param Title
     * @param Text
     * @param Icon the Spritename of an icon that should be used.
     */
    @JvmStatic
    fun createIntelPopup(Title: String, Text: String, Icon: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.icon = Icon
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

    /**
    *Gets a copy of all markets from a faction.
    */
    @JvmStatic
    fun getFactionMarkets(FactionId: String) : List<MarketAPI>
    {
        val list: MutableList<MarketAPI> = ArrayList()
        Global.getSector().economy.marketsCopy.forEach { if (it.factionId == FactionId) list.add(it) }
        return list
    }
}