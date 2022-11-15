package lunalib.lunaWrappers

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import org.lwjgl.util.vector.Vector2f

//Inspired by Wisps Memory delegate, but in a format more useable in Java.

 /**
 * Class that gets instantiated with an ID, and can then be used to put data in to memory under that ID.
 * Avoids having to create references to the MemoryAPI multiple times in a class.
 * @param id ID of the memory setting. Should just be the ID, without a $ sign.
 */
class LunaMemory(id: String)
{
    private var Id = id

    fun set(value: Any?)
    {
        Global.getSector().memoryWithoutUpdate.set("\$$Id", value)
    }

    fun get() : Any?
    {
        return Global.getSector().memoryWithoutUpdate.get("\$$Id")
    }

    fun getInt() : Int?
    {
        return Global.getSector().memoryWithoutUpdate.getInt("\$$Id")
    }

    fun getFloat() : Float?
    {
        return Global.getSector().memoryWithoutUpdate.getFloat("\$$Id")
    }

    fun getString() : String?
    {
        return Global.getSector().memoryWithoutUpdate.getString("\$$Id")
    }

    fun getBoolean() : Boolean?
    {
        return Global.getSector().memoryWithoutUpdate.getBoolean("\$$Id")
    }

    fun getLong() : Long?
    {
        return Global.getSector().memoryWithoutUpdate.getLong("\$$Id")
    }

    fun getVector2f() : Vector2f?
    {
        return Global.getSector().memoryWithoutUpdate.getVector2f("\$$Id")
    }

    fun getFleet() : CampaignFleetAPI?
    {
        return Global.getSector().memoryWithoutUpdate.getFleet("\$$Id")
    }

    fun getEntity() : SectorEntityToken?
    {
        return Global.getSector().memoryWithoutUpdate.getEntity("\$$Id")
    }

    fun expire(days: Float)
    {
        Global.getSector().memoryWithoutUpdate.expire("\$$Id", days)
    }
}