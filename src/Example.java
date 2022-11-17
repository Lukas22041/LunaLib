import lunalib.lunaUtil.LunaUtils;
import java.awt.*;

public class Example
{
    public void exampleMethod()
    {
        float saturation = 1f;
        int alpha = 255;

        //Generates a random color with the saturation provided.
        Color randomColor = LunaUtils.randomColor(saturation, alpha);

        //Creates a small notification in the bottom left of the UI.
        LunaUtils.createIntelMessage("Title", "Message");
    }
}
