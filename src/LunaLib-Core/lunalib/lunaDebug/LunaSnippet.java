package lunalib.lunaDebug;

import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LunaSnippet
{
    public enum SnippetTags {
        Cheat, Debug, Cargo, Entity, Player, Faction
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getModId();

    public abstract List<String> getTags();


    public void addParameters(SnippetBuilder builder) {

    }

    public void execute(Map<String, Object> parameters, TooltipMakerAPI output) {

    }
}
