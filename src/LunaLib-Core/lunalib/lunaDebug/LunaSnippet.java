package lunalib.lunaDebug;

import java.util.List;
import java.util.Map;

public interface LunaSnippet
{
    enum SnippetCategory {
        Cheat, Debug, Cargo, Entity, Player, Faction
    }

    public String getName();
    public String getDescription();
    public String getModId();
    public List<SnippetCategory> getCategories();
    public void addParameters(SnippetBuilder builder);
    public String execute(Map<String, Object> parameters);
}
