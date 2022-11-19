package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import lunalib.lunaExtensions.getArray
import lunalib.lunaExtensions.getList
import org.lwjgl.util.vector.Vector2f

//Inspired by Wisps Memory delegate, but in a format more useable in Java.

 /**
 A utility class that can be used to interact with the MemoryAPI more conveniently.
 No data, except the [Key] is stored within the LunaMemory object, which is then used to make calls to MemoryAPI to get the value from that MemKey.

 [LunaMemory on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaMemory)
 ```java
 //Instantiate it with an ID.
 LunaMemory message = new LunaMemory("VariableKey");

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
  LunaMemory message = new LunaMemory("VariableKey", "A default String stored in the playerfleet!", Global.getSector().getPlayerFleet());
  ```

 [LunaMemory on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaMemory)


  */
class LunaMemory(key: String)
{

    private var Key = key
    private var Target: SectorEntityToken? = null

    //Alternate Constructor that sets a default value for the ID when innitialised, if the memory is currently null.
    constructor(key: String, default: Any?) : this(key)
    {
        if (Global.getSector().memoryWithoutUpdate.get("\$$Key") == null) Global.getSector().memoryWithoutUpdate.set("\$$Key", default)
    }

    //Alternate Constructor that causes memory to be set on a specific entities local memory
    constructor(key: String, target: SectorEntityToken) : this(key)
    {
        Target = target
    }

    //Alternate Constructor that causes memory to be set on a specific entities local memory, and sets a default value on to it if the ID returns null.
    constructor(key: String, default: Any?, target: SectorEntityToken) : this(key)
    {
        Target = target
        if (Target != null)
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$$Key") == null) Target!!.memoryWithoutUpdate.set("\$$Key", default)
        }
        else
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$$Key") == null) Global.getSector().memoryWithoutUpdate.set("\$$Key", default)
        }
    }

    fun set(value: Any?)
    {
        if (Target != null)
        {
            Target!!.memoryWithoutUpdate.set("\$$Key", value)
        }
        else
        {
            Global.getSector().memoryWithoutUpdate.set("\$$Key", value)
        }
    }

    fun get() : Any?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key")
        }
    }

    fun getInt() : Int?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getInt("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getInt("\$$Key")
        }
    }

    fun getFloat() : Float?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getFloat("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getFloat("\$$Key")
        }
    }

    fun getString() : String
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getString("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getString("\$$Key")
        }
    }

    fun getBoolean() : Boolean?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getBoolean("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getBoolean("\$$Key")
        }
    }

    fun getLong() : Long?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getLong("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getLong("\$$Key")
        }
    }

    fun getVector2f() : Vector2f?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getVector2f("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getVector2f("\$$Key")
        }
    }

    fun getFleet() : CampaignFleetAPI?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getFleet("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getFleet("\$$Key")
        }
    }

    fun getEntity() : SectorEntityToken?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.getEntity("\$$Key")
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.getEntity("\$$Key")
        }
    }

    fun getList() : List<*>?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key") as List<*>?
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key") as List<*>?
        }
    }

    fun getArray() : Array<*>?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key") as Array<*>?
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key") as Array<*>?
        }
    }

    fun getMap() : Map<*, *>?
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key") as Map<*, *>?
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key") as Map<*, *>?
        }
    }

    fun expire(days: Float)
    {
        if (Target != null)
        {
            Target!!.memoryWithoutUpdate.expire("\$$Key", days)
        }
        else
        {
            Global.getSector().memoryWithoutUpdate.expire("\$$Key", days)

        }
    }

    fun isNull() : Boolean
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key") == null
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key") == null
        }
    }

    fun isNotNull() : Boolean
    {
        if (Target != null)
        {
            return Target!!.memoryWithoutUpdate.get("\$$Key") != null
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$$Key") != null
        }
    }
}