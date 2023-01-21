package lunalib.lunaCMDs

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.util.Misc

class LunaSubmarketCMD : BaseCommandPlugin() {

    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean
    {
        val marketEntity: SectorEntityToken? = Global.getSector().getEntityById(memoryMap?.get("local")?.getString("\$id"))
        if (marketEntity == null) return false
        val market: MarketAPI = marketEntity.market

        val argument = params!![0].getString(memoryMap)
        val id = params[1].getString(memoryMap)

        when (argument)
        {
            "has" -> return market.hasSubmarket(id)
            "add" -> if (!market.hasSubmarket(id)) market.addSubmarket(id)
            "remove" -> if (market.hasSubmarket(id)) market.removeSubmarket(id)
            "toggle" -> {
                if (!market.hasSubmarket(id)) market.addSubmarket(id)
                else market.removeSubmarket(id)
            }
        }
        return false
    }
}