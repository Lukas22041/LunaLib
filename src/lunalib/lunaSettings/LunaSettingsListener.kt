package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global

//Listener used to check if settings changed. Only gets called from the Mod Menu in the campaign.
//Has to be added through Global.getSector().listenerManager.addListener()
/**
* Listener used to check if LunaSettings got changed. Only gets called from the Mod Menu in the campaign.
 *
* Has to be added through Global.getSector().listenerManager.addListener()
*/




/**
Listener for LunaSettings. [settingsChanged] gets called whenever settings are saved in the campaign.

[LunaSettingsListener on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaSettingsListener)

```Java
public class Example implements LunaSettingsListener
{
*   public Example()
*   {
*   Global.getSector().getListenerManager().addListener(this, true);
*   }

*   float exampleDouble = LunaSettings.getDouble("lunalib", "luna_testDouble", false);

*   @Override
*   public void settingsChanged() {
*   exampleDouble = LunaSettings.getDouble("lunalib", "luna_testDouble", false);
*   }
}
 ```
 */
interface LunaSettingsListener {
    abstract fun settingsChanged()
}

