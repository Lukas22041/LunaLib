import com.fs.starfarer.api.Global;
import lunalib.lunaUtil.LunaMisc;
import lunalib.lunaWrappers.LunaMemory;

public class IntelMessage
{
    LunaMemory message = new LunaMemory("VariableID", Global.getSector().getPlayerFleet());

    //Puts a value in to memory
    public void setIntelMessage()
    {
        message.set("A test String stored in the player fleets memory");
    }

    //Sends an intel message using the variable in memory.
    public void sendIntelMessage()
    {
        String string = message.getString();
        LunaMisc.intelPopup("Title", string);
    }
}
