package lunalib.lunaDebug.snippets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaDebug.LunaSnippet;
import lunalib.lunaDebug.SnippetBuilder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModListSnippet extends LunaSnippet {

    //The name displayed on top of the Snippet.
    @Override
    public String getName() {
        return "Copy modlist to the clipboard";
    }

    //The Description of the Snippet. The length of the Description slightly increases the size of the Snippet, but not by a lot.
    //If your Snippet has a long description and not many parameters, use the builder.addSpace(); method in addParameters to increase the size of the Snippet.
    @Override
    public String getDescription() {
        return "Copies a list of all loaded mods on to the clipboard. Can be toggled in to a more discord-friendly format.";
    }

    //Required to display which mod the Snippet is from.
    @Override
    public String getModId() {
        return "lunalib";
    }

    //Represents the tags on the left side of the Snippet Menu. The SnippetTags Enum provides the main preset Tags available, but returning a Unique String will
    //create a new Tag and add it to the list.
    @Override
    public List<String> getTags() {
        List<String> tags = new ArrayList<>();
        tags.add(SnippetTags.Debug.toString());
        //tags.add("Lunalib");
        return tags;
    }

    //Called when the Snippet is being created. It allows you to add Parameters to the Snippet. The "Key" String can be received in the execute method.
    @Override
    public void addParameters(SnippetBuilder builder) {
        builder.addBooleanParameter("Discord Format", "Discord", false);
    }

    //Called when the "Execute" has been pressed.
    //The parameters variable contains the value of all parameters added from addParameters, getabble through their key String.
    //The output variable allows to add paragraphs, images, etc to the output panel.
    @Override
    public void execute(Map<String, Object> parameters, TooltipMakerAPI output) {
        Boolean discord = (Boolean) parameters.get("Discord");

        List<ModSpecAPI> mods = Global.getSettings().getModManager().getEnabledModsCopy();
        StringBuilder modList = new StringBuilder();

        //Creating a new Paragraph at the top of the Output Window.
        if (discord)
        {
            output.addPara("Succesfully copied to clipboard with Discord formatting!", 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor());
        }
        else
        {
            output.addPara("Succesfully copied to clipboard!", 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor());
        }
        output.addSpacer(5f);
        for (ModSpecAPI mod : mods)
        {
            if (discord)
            {
                modList.append("**" + mod.getName() + ":** ``" + mod.getVersion() + "``\n");
            }
            else
            {
                modList.append(mod.getName() + ":" + mod.getVersion() + "\n");
            }
            //Creating a new Paragraph in the output window for each line, and then highlighting the Mod name.
            LabelAPI paragraph = output.addPara(mod.getName() + ":" + mod.getVersion(), 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor());
            paragraph.setHighlight(mod.getName());
        }

        StringSelection stringSelection = new StringSelection(modList.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
