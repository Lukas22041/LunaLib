package lunalib.lunaDebug;

import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.List;
import java.util.Map;

public interface LunaSnippet
{
    enum SnippetTags {
        Cheat, Debug, Cargo, Entity, Player, Faction
    }

    public String getName();
    public String getDescription();
    public String getModId();
    public List<String> getTags();
    public void addParameters(SnippetBuilder builder);
    public void execute(Map<String, Object> parameters, TooltipMakerAPI output);
}
