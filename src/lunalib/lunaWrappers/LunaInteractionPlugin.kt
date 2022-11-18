package lunalib.lunaWrappers

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.BattleCreationContext
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.FleetAdvanceScript
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import java.awt.Color

/**
Wrapper for [InteractionDialogPlugin].

[LunaInteractionPlugin on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaInteractionPlugin)

It presets a variety of commonly used variables on innitialisation, reducing the large bloat that most IDPs have in their init() method.
The Class also comes with some utility methods.

[LunaInteractionPlugin] only has one abstract method, which is called [start].
 All other InteractionDialogPlugins, except init() can still be overriden.

```Java
public class Example extends LunaInteractionPlugin
{
*   @Override
*   public void start()
*   {
*   textPanel.addPara("A new dialog, woo!", getPlayerColor(), getHighlightColor(), "dialog");
*   addLeaveOption();
*   }
}
```


*/
abstract class LunaInteractionPlugin() : InteractionDialogPlugin
{
    // General variables that are commonly used
    lateinit var dialog: InteractionDialogAPI
    lateinit var textPanel: TextPanelAPI
    lateinit var optionPanel: OptionPanelAPI
    lateinit var visualPanel: VisualPanelAPI
    lateinit var interactionTarget: SectorEntityToken
    lateinit var targetMemory: MemoryAPI
    lateinit var memory: MemoryAPI

    // Preset Color variables to make accessing those easier
    val playerColor = Misc.getBasePlayerColor()
    var brightPlayerColor = Misc.getBrightPlayerColor()
    var darkPlayercolor = Misc.getDarkPlayerColor()
    var highlightColor = Misc.getHighlightColor()
    var positiveColor = Misc.getPositiveHighlightColor()
    var negativeColor = Misc.getNegativeHighlightColor()
    var textColor = Misc.getTextColor()

    //Innitiates and executes the main dialog method
    final override fun init(dialog: InteractionDialogAPI) {
        this.dialog = dialog
        this.textPanel = dialog.textPanel
        this.optionPanel = dialog.optionPanel
        this.visualPanel = dialog.visualPanel
        this.interactionTarget = dialog.interactionTarget
        this.targetMemory = dialog.interactionTarget.memoryWithoutUpdate
        this.memory = Global.getSector().memoryWithoutUpdate

        if (dialog.interactionTarget.customInteractionDialogImageVisual != null)
        {
            visualPanel.showImageVisual(dialog.interactionTarget.customInteractionDialogImageVisual)
        }

        start()
    }

    /**
     *Gets called after initialisation
     */
    abstract fun start()

    override fun optionSelected(optionText: String?, optionData: Any?)
    {
        if (optionData is String)
        {
            if (optionData == "LEAVE") closeDialog()
            if (optionData == "BACKTOSTART") reset()
        }
    }

    /**
    *Resets to start page and clears panels
    */
    final fun reset()
    {
        textPanel.clear()
        optionPanel.clearOptions()
        start()
    }

    /**
     *Clears option and text panels
     */
    final fun clear()
    {
        textPanel.clear()
        optionPanel.clearOptions()
    }

     /**
     *Closes the dialog
     */
    final fun closeDialog()
    {
        DismissDialog().execute(null, dialog, null, null)
    }

     /**
     *Adds an option bringing the dialog back to the mainPage
     */
    final fun addBackToStartOption()
    {
        optionPanel.addOption("Back", "BACKTOSTART");
        optionPanel.setShortcut("BACKTOSTART", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

     /**
     *Adds an option for dismissing the dialog
     */
    final fun addLeaveOption()
    {
        optionPanel.addOption("Leave", "LEAVE");
        optionPanel.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

     /**
     *Creates a visual cost panel for commodities. The return value can be used to disable/enable options. Does not handle removing the commodities.
     * @param commodities map of CommodityIDs and the cost for them.
      *@param border creates a border around the cost, not recommended for anything including more than 3 commodities.
     * @return returns true if the player meets the cost
     */
    final fun createCostPanel(commodities: Map<String, Int>, border: Boolean) : Boolean
    {

        var cost: ResourceCostPanelAPI = textPanel.addCostPanel("", 67f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor())
        var cargo = Global.getSector().playerFleet.cargo

        var slots = 0
        var metConditions = true
        for (commodity in commodities)
        {
            if (slots > 2)
            {
                cost = textPanel.addCostPanel("", 67f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor())
                slots = 0
            }
            slots++


            val available: Int = cargo.getCommodityQuantity(commodity.key).toInt()
            var color: Color? = Misc.getPositiveHighlightColor()

            if (commodity.value > available) {
                color = Misc.getNegativeHighlightColor();
                metConditions = false
            }

            cost.setNumberOnlyMode(true);
            cost.alignment = Alignment.MID
            cost.isWithBorder = border

            cost.addCost(commodity.key, "" + commodity.value + " (" + available + ")", color);
            cost.update()

        }


        return metConditions
    }

    /**
     * Triggers defenders if $defenderFleet is set to a fleet in the memory of the target.
     * Once defeated, it will trigger the defeatedDefenders() method.
     */
    final fun triggerDefenders()
    {
        if (targetMemory.getFleet("\$defenderFleet") == null) return

        val entity = dialog.interactionTarget
        val defenders = targetMemory.getFleet("\$defenderFleet")

        dialog.interactionTarget = defenders

        val config = FIDConfig()
        config.leaveAlwaysAvailable = true
        config.showCommLinkOption = false
        config.showEngageText = false
        config.showFleetAttitude = false
        config.showTransponderStatus = false
        config.showWarningDialogWhenNotHostile = false
        config.alwaysAttackVsAttack = true
        config.impactsAllyReputation = true
        config.impactsEnemyReputation = false
        config.pullInAllies = false
        config.pullInEnemies = false
        config.pullInStations = false
        config.lootCredits = false

        config.firstTimeEngageOptionText = "Engage the defenses"
        config.afterFirstTimeEngageOptionText = "Re-engage the defenses"
        config.noSalvageLeaveOptionText = "Continue"

        config.dismissOnLeave = false
        config.printXPToDialog = true

        val seed = targetMemory.getLong(MemFlags.SALVAGE_SEED)
        config.salvageRandom = Misc.getRandom(seed, 75)

        val plugin = FleetInteractionDialogPluginImpl(config)

        val originalPlugin = dialog.plugin

        config.delegate = FIDOverride(defenders, dialog, plugin, originalPlugin, this)

        dialog.plugin = plugin
        plugin.init(dialog)
    }

    /**
     * gets called after defenders were beat
     */
    open fun defeatedDefenders()
    {

    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI>? {
        return null
    }


}


class FIDOverride(defenders: CampaignFleetAPI, dialog: InteractionDialogAPI, plugin: FleetInteractionDialogPluginImpl, originalPlugin: InteractionDialogPlugin, LunaInt: LunaInteractionPlugin) : FleetInteractionDialogPluginImpl.BaseFIDDelegate()
{

    var defenders: CampaignFleetAPI = defenders
    var LunInteraction = LunaInt

    var plugin = plugin
    var originalPlugin = originalPlugin

    override fun notifyLeave(dialog: InteractionDialogAPI) {
        // nothing in there we care about keeping; clearing to reduce savefile size
        val entity = dialog.interactionTarget
        defenders.getMemoryWithoutUpdate().clear()
        // there's a "standing down" assignment given after a battle is finished that we don't care about
        defenders.clearAssignments()
        defenders.deflate()
        var memory = dialog.interactionTarget.memoryWithoutUpdate
        dialog.plugin = originalPlugin
        dialog.interactionTarget = entity

        //Global.getSector().getCampaignUI().clearMessages();
        if (plugin.getContext() is FleetEncounterContext) {
            val context = plugin.getContext() as FleetEncounterContext
            if (context.didPlayerWinEncounterOutright()) {
                val p = SDMParams()
                p.entity = entity
                p.factionId = defenders.getFaction().getId()
                val plugin =
                    Global.getSector().genericPlugins.pickPlugin(SalvageDefenderModificationPlugin::class.java, p)
                plugin?.reportDefeated(p, entity, defenders)
                memory.unset("\$hasDefenders")
                memory.unset("\$defenderFleet")
                memory.set("\$defenderFleetDefeated", true)
                entity.removeScriptsOfClass(FleetAdvanceScript::class.java)
                LunInteraction.defeatedDefenders()
            } else {
                var persistDefenders = false
                if (context.isEngagedInHostilities) {
                    persistDefenders = persistDefenders or !Misc.getSnapshotMembersLost(defenders).isEmpty()
                    for (member in defenders.getFleetData().getMembersListCopy()) {
                        if (member.status.needsRepairs()) {
                            persistDefenders = true
                            break
                        }
                    }
                }
                if (persistDefenders) {
                    if (!entity.hasScriptOfClass(FleetAdvanceScript::class.java)) {
                        defenders.setDoNotAdvanceAI(true)
                        defenders.setContainingLocation(entity.getContainingLocation())
                        // somewhere far off where it's not going to be in terrain or whatever
                        defenders.setLocation(1000000f, 1000000f)
                        entity.addScript(FleetAdvanceScript(defenders))
                    }
                    memory.expire("\$defenderFleet", 10f) // defenders may have gotten damaged; persist them for a bit
                }
                dialog.dismiss()
            }
        } else {
            dialog.dismiss()
        }
    }

    override fun battleContextCreated(dialog: InteractionDialogAPI?, bcc: BattleCreationContext) {
        bcc.aiRetreatAllowed = false
        bcc.objectivesAllowed = false
        bcc.enemyDeployAll = true
    }
}

