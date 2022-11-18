package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import org.lwjgl.util.vector.Vector2f

//Inspired by Wisps Memory delegate, but in a format more useable in Java.

 /**
 A utility class that can be used to interact with the MemoryAPI more conveniently.
 No data, except the [Id] is stored within the LunaMemory object, which is then used to make calls to MemoryAPI to get the value from that ID.

 [LunaMemory on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaMemory)
 ```java
 //Instantiate it with an ID.
 LunaMemory message = new LunaMemory("VariableID");

 //Puts a value in to memory
 public void setIntelMessage()
 {
 message.set("A test String");
 }

 //Sends an intel message using the variable in memory.
 public void sendIntelMessage()
 {
 String string = message.getString();
 LunaUtil.createIntelMessage("Title", string);
 }
 ```

 The Class' Constructor also accepts a default variable, which can be used to avoid Nullpointer exceptions occuring from having no variable set.
 It also accepts a SectorEntityToken, which causes LunaMemory to save its data in the local memory of that entity, instead of Globaly.
 ```Java
  LunaMemory message = new LunaMemory("VariableID", "A default String stored in the playerfleet!", Global.getSector().getPlayerFleet());
  ```

 [LunaMemory on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaMemory)


  */
class LunaMemory(id: String)
{
    private var Id = id
    private var Target: SectorEntityToken? = null

    //Alternate Constructor that sets a default value for the ID when innitialised, if the memory is currently null.
    constructor(id: String, default: Any?) : this(id)
    {
        if (Global.getSector().memoryWithoutUpdate.get("\$$Id") == null) Global.getSector().memoryWithoutUpdate.set("\$$Id", default)
    }

    //Alternate Constructor that causes memory to be set on a specific entities local memory
    constructor(id: String, target: SectorEntityToken) : this(id)
    {
        Target = target
    }

    //Alternate Constructor that causes memory to be set on a specific entities local memory, and sets a default value on to it if the ID returns null.
    constructor(id: String, default: Any?, target: SectorEntityToken) : this(id)
    {
        Target = target

        if (Target != null)
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$$Id") == null) Target!!.memoryWithoutUpdate.set("\$$Id", default)
        }
        else
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$$Id") == null) Global.getSector().memoryWithoutUpdate.set("\$$Id", default)
        }
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