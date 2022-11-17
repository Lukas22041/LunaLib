package lunalib;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class Example implements LunaSettingsListener {

    //Adds the listener when the class gets innitialized
    public Example()
    {
        //Adds the Listener
        //Has to getSector().getListenerManager().addListener, not getSector().addListener!!!
        //The "true" sets it to transient, meaning the listener wont get saved in to the save, use this if the Class gets innitialized on Load.
        Global.getSector().getListenerManager().addListener(this, true);
    }

    float exampleFloat = LunaSettings.getFloat("lunalib", "luna_testFloat", false);

    //Gets called whenever settings are saved while in the Campaign.
    @Override
    public void settingsChanged() {
        exampleFloat = LunaSettings.getFloat("lunalib", "luna_testFloat", false);
    }
}
