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
    private var Target: SectorEntityToken? = null

    constructor(id: String, target: SectorEntityToken) : this(id)
    {
        Target = target
    }

    fun set(value: Any?)
    {
        if (Target != null)
        {
            Target!!.memoryWithoutUpdate.set("\$$Id", value)
        }
        else
        {
            Global.getSector().memoryWithoutUpdate.set("\$$Id", value)
        }
    }

    fun get() : Any?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Id")
        }
    }

    fun getInt() : Int?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getInt("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getInt("\$$Id")
        }
    }

    fun getFloat() : Float?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getFloat("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getFloat("\$$Id")
        }
    }

    fun getString() : String
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getString("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getString("\$$Id")
        }
    }

    fun getBoolean() : Boolean?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getBoolean("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getBoolean("\$$Id")
        }
    }

    fun getLong() : Long?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getLong("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getLong("\$$Id")
        }
    }

    fun getVector2f() : Vector2f?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getVector2f("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getVector2f("\$$Id")
        }
    }

    fun getFleet() : CampaignFleetAPI?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getFleet("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getFleet("\$$Id")
        }
    }

    fun getEntity() : SectorEntityToken?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getEntity("\$$Id")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getEntity("\$$Id")
        }
    }

    fun expire(days: Float)
    {
        if (Target != null)
        {
            Target!!.memoryWithoutUpdate.expire("\$$Id", days)
        }
        else
        {
            Global.getSector().memoryWithoutUpdate.expire("\$$Id", days)

        }
    }
}