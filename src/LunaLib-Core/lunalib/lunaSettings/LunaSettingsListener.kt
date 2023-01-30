package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global

/**
Listener for LunaSettings. [settingsChanged] gets called whenever settings are saved in the campaign.

[LunaSettingsListener on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaSettingsListener)

```Java
public class Example implements LunaSettingsListener
{
 *   double exampleDouble = LunaSettings.getDouble("lunalib", "luna_testDouble");

 *   public Example()
*   {
*   Global.getSector().getListenerManager().addListener(this, true);
*   }

*   @Override
*   public void settingsChanged() {
*   exampleDouble = LunaSettings.getDouble("lunalib", "luna_testDouble");
*   }
}
 ```
 */
interface LunaSettingsListener {
    abstract fun settingsChanged(modID: String)
}

