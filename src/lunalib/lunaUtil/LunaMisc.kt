package lunalib.lunaUtil

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import java.awt.Color
import java.text.DecimalFormat
import kotlin.random.Random

object LunaMisc
{
    private var timeCodes: MutableMap<String, Long> = HashMap<String, Long>()

    init {
        var memory: MemoryAPI = Global.getSector().memoryWithoutUpdate

        if (memory.get("\$luna_timer") != null) timeCodes = memory.get("\$luna_timer") as MutableMap<String, Long>
    }

    //CampaignTimer
    @JvmStatic
    fun addCampaignTimer(id: String)
    {
        var memory: MemoryAPI = Global.getSector().memoryWithoutUpdate
        timeCodes.put(id, Global.getSector().clock.timestamp)
        memory.set("\$luna_timer", timeCodes)
    }

    @JvmStatic
    fun getCampaignTimer(id: String) : Float
    {
        var memory: MemoryAPI = Global.getSector().memoryWithoutUpdate
        memory.get("\$luna_timer") ?: return 0f

        val format: DecimalFormat = DecimalFormat("#.##")
        timeCodes = memory.get("\$luna_timer") as MutableMap<String, Long>
        var timeSince: Float = Global.getSector().clock.getElapsedDaysSince(timeCodes.get(id)!!)

        timeSince = format.format(timeSince).toFloat()

        return timeSince
    }

    @JvmStatic
    fun removeCampaignTimer(id: String)
    {
        var memory: MemoryAPI = Global.getSector().memoryWithoutUpdate
        timeCodes.remove(id) ?: return
        memory.set("\$luna_timer", timeCodes)
    }

    @JvmStatic
    fun randomColor(saturation: Float) : Color
    {
        var hue = Random.nextFloat()
        var luminance = 0.9f
        var color = Color.getHSBColor(hue, saturation, luminance)



        return color
    }

    //Campaign Intel Pop up
    @JvmStatic
    fun intelPopup(Title: String, Text: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        //intel.icon = Global.getSettings().getSpriteName("intel", "discovered_entity")
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

    @JvmStatic
    fun intelPopup(Title: String, Text: String, Icon: String)
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
    fun getFactionMarket(FactionId: String) : List<MarketAPI>
    {
        var list: MutableList<MarketAPI> = ArrayList()
        Global.getSector().economy.marketsCopy.forEach { if (it.factionId == FactionId) list.add(it) }
        return list
    }
}