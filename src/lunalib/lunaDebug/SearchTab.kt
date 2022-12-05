package lunalib.lunaDebug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination
import com.fs.starfarer.api.fleet.FleetAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TextFieldAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CustomCampaignEntity
import lunalib.lunaSettings.LunaSettings
import org.json.JSONObject
import java.awt.Button
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


internal class SearchTab(panel: CustomPanelAPI, dialog: InteractionDialogAPI) : DebugTab(panel, dialog)
{
    override var panelName = "Search"

    var start = true

    var searchElement: TooltipMakerAPI? = null
    var searchBar: TextFieldAPI? = null

    var filterButtons: MutableMap<String, ButtonAPI> = HashMap()
    var checkedFilterButtons: MutableMap<ButtonAPI, Boolean> = HashMap()

    var entitiesPanel: CustomPanelAPI? = null
    var entitiesElement: TooltipMakerAPI? = null
    var entities: MutableList<Any> = ArrayList()

    var previousString = "."

    var teleportButtons: MutableMap<SectorEntityToken, ButtonAPI> = HashMap()

    var filters = listOf("Starsystems", "Stars", "Planets" ,"Custom Entities", "Fleets")
    companion object
    {
        var lastString = ""
        var checkedFilters = mutableMapOf("Starsystems" to true, "Stars" to true, "Planets" to true, "Custom Entities" to true, "Fleets" to true)
    }

    var cap = 25

    override fun init()
    {
        cap = LunaSettings.getInt("lunalib", "luna_DebugEntries", false)!!

        searchPanel()
        entitiesPanel()
    }

    fun searchPanel()
    {
        searchElement = panel.createUIElement(pWidth, pHeight, false)

        var spacingPara = searchElement!!.addPara("Search for Entities (Name/Id/Tags)", 0f)
        spacingPara.position.inTL(pWidth * 0.04f, pHeight * 0.20f)

        searchBar = searchElement!!.addTextField(pWidth * 0.2f, 0f)
        searchBar!!.text = lastString

        var spacing = pHeight * 0.3f

        for (filter in filters)
        {
            val button: ButtonAPI = searchElement!!.addAreaCheckbox(filter, null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                pWidth * 0.2f,
                pHeight * 0.05f,
                0f)
            button.position.inTL(pWidth * 0.04f, spacing)
            button.isChecked = checkedFilters.get(filter)!!
            filterButtons.put(filter, button)
            spacing += button.position.height
        }

        panel.addUIElement(searchElement)
    }

    fun entitiesPanel()
    {
        entitiesPanel = panel.createCustomPanel(pWidth * 0.7f, pHeight * 0.8f, null)
        entitiesPanel!!.position.setLocation(0f, 0f).inTL(pWidth * 0.27f, pHeight * 0.15f)

        entitiesElement = entitiesPanel!!.createUIElement(pWidth * 0.7f, pHeight * 0.8f, true)

        var spacing = 0f

        for (entity in entities)
        {
            val spacer: ButtonAPI = entitiesElement!!.addAreaCheckbox("", null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                entitiesPanel!!.position.width,
                pHeight * 0.2f,
                0f)

            spacer.isEnabled = false
            spacer.position.inTL(0f, spacing)

            addEntityToPanel(entity, spacing, spacer)

            spacing += pHeight * 0.2f
        }

        entitiesPanel!!.addUIElement(entitiesElement)
        panel.addComponent(entitiesPanel)
        panelsToOutline.add(entitiesPanel!!)
    }

    private fun addEntityToPanel(entity: Any, spacing: Float, spacer: ButtonAPI)
    {
        var offset = pHeight * 0.2f
        when (entity)
        {
            is StarSystemAPI ->
            {
                var tooltip = TooltipPreset("" +
                        "Name: ${entity.name}\n" +
                        "Id: ${entity.id}\n" +
                        "Tags: ${entity.tags.toString()}\n" +
                        "Scripts: ${entity.scripts.toString()}", 500f, "Name", "Id", "Tags:", "Scripts: ")

                entitiesElement!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

                var name = entitiesElement!!.addPara("${entity.name} (System)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                entitiesElement!!.addPara("Id: ${entity.id}", Misc.getBasePlayerColor(), 0f)

                if (entity.center.isStar)
                {
                    entitiesElement!!.addPara("Primary Star: ${entity.star.name}", Misc.getBasePlayerColor(), 0f)
                    entitiesElement!!.addImage(entity.star.spec.texture, 128f, 84f, 0f)
                }

                val teleport: ButtonAPI = entitiesElement!!.addAreaCheckbox("Teleport", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                teleport.position.inTL(pWidth * 0.4f,(spacing + offset / 2) - name.position.height)
                teleportButtons.put(entity.center, teleport)
            }
            is PlanetAPI ->
            {
                var tooltip = TooltipPreset("" +
                        "Name: ${entity.name}\n" +
                        "Id: ${entity.id}\n" +
                        "Type: ${entity.spec.planetType}\n" +
                        "Tags: ${entity.tags.toString()}", 500f, "Name", "Id", "Type:", "Tags:")

                entitiesElement!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

                var nameString = "Planet"
                if (entity.isStar) nameString = "Star"

                var name = entitiesElement!!.addPara("${entity.name} ($nameString)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                entitiesElement!!.addPara("Id: ${entity.id}", Misc.getBasePlayerColor(), 0f)

                entitiesElement!!.addImage(entity.spec.texture, 128f, 84f, 0f)

                val teleport: ButtonAPI = entitiesElement!!.addAreaCheckbox("Teleport", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                teleport.position.inTL(pWidth * 0.4f,(spacing + offset / 2) - name.position.height)
                teleportButtons.put(entity, teleport)
            }
            is CustomCampaignEntity ->
            {
                var tooltip = TooltipPreset("" +
                        "Name: ${entity.name}\n" +
                        "Id: ${entity.id}\n" +
                        "Type: ${entity.spec.id}\n" +
                        "Tags: ${entity.tags.toString()} \n" +
                        "Plugin: ${entity.customPlugin.toString()}", 500f, "Name", "Id", "Type", "Tags:", "Plugin:")

                entitiesElement!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

                var name = entitiesElement!!.addPara("${entity.name} (Custom Entity)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                entitiesElement!!.addPara("Id: ${entity.id}", Misc.getBasePlayerColor(), 0f)

                entitiesElement!!.addPara("", 0f)

                entitiesElement!!.addImage(entity.spec.iconName, 64f, 64f, 0f)


                val teleport: ButtonAPI = entitiesElement!!.addAreaCheckbox("Teleport", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                teleport.position.inTL(pWidth * 0.4f,(spacing + offset / 2) - name.position.height)
                teleportButtons.put(entity, teleport)
            }
            is CampaignFleetAPI ->
            {
                var tooltip = TooltipPreset("" +
                        "Name: ${entity.name}\n" +
                        "Id: ${entity.id}\n" +
                        "Tags: ${entity.tags.toString()}\n" +
                        "Scripts: ${entity.scripts.toString()}", 500f, "Name", "Id", "Tags:", "Scripts: ")

                entitiesElement!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

                var name = entitiesElement!!.addPara("${entity.name} (Fleet)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                entitiesElement!!.addPara("Id: ${entity.id}", Misc.getBasePlayerColor(), 0f)

                entitiesElement!!.addShipList(6, 1, 64f, Misc.getBasePlayerColor(), entity.fleetData.membersListCopy, 0f)

                val teleport: ButtonAPI = entitiesElement!!.addAreaCheckbox("Teleport", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                teleport.position.inTL(pWidth * 0.4f,(spacing + offset / 2) - name.position.height)
                teleportButtons.put(entity, teleport)
            }
        }
    }

    override fun advance(amount: Float)
    {

        var filterChanged = false

        for (button in filterButtons)
        {
            if (button.value.isChecked != checkedFilterButtons.get(button.value))
            {
                filterChanged = true
            }
            checkedFilterButtons.set(button.value, button.value.isChecked)
        }

        for (filter in filters)
        {
            for (button in filterButtons)
            {
                if (button.key == filter)
                {
                    checkedFilters.put(button.key, button.value.isChecked)
                }
            }
        }

        if (searchBar!!.text != previousString || start || filterChanged)
        {
            start = false
            previousString = searchBar!!.text
            lastString = searchBar!!.text
            entities.clear()

            var filters = searchBar!!.text.split(",")

            var capCount = 0

            var systems = Global.getSector().starSystems
            var planets: MutableList<PlanetAPI> = ArrayList()
            var fleets: MutableList<CampaignFleetAPI> = ArrayList()
            var customEntities: MutableList<SectorEntityToken> = ArrayList()

            for (system in systems)
            {
                planets.addAll(system.planets)
                fleets.addAll(system.fleets)
                customEntities.addAll(system.customEntities)
            }

            for (button in filterButtons)
            {
                for (filt in filters)
                {

                    var filter = filt.trim().lowercase()

                    if (button.key == "Starsystems" && button.value.isChecked)
                    {
                        run search@ {
                            systems.forEach {
                                if (it.name.lowercase().contains(filter) || it.id.lowercase().contains(filter) || it.tags.any { tag -> tag.lowercase().contains(filter) }) { entities.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }

                    if (button.key == "Stars" && button.value.isChecked)
                    {
                        run search@ {
                            planets.forEach {
                                if (!it.isStar) return@forEach
                                if (it.name.lowercase().contains(filter) || it.id.lowercase().contains(filter) || it.tags.any { tag -> tag.lowercase().contains(filter) }) { entities.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }

                    if (button.key == "Planets" && button.value.isChecked)
                    {
                        run search@ {
                            planets.forEach {
                                if (it.isStar) return@forEach
                                if (it.name.lowercase().contains(filter) || it.id.lowercase().contains(filter) || it.tags.any { tag -> tag.lowercase().contains(filter) } || it.faction.id.lowercase().contains(filter) || it.faction.displayName.lowercase().contains(filter)) { entities.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }

                    if (button.key == "Custom Entities" && button.value.isChecked)
                    {
                        run search@ {
                            customEntities.forEach {
                                if (it.tags.contains("orbital_junk")) return@forEach
                                if (it.name.lowercase().contains(filter) || it.id.lowercase().contains(filter) || it.tags.any { tag -> tag.lowercase().contains(filter) } || it.faction.id.lowercase().contains(filter) || it.faction.displayName.lowercase().contains(filter)) { entities.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }

                    if (button.key == "Fleets" && button.value.isChecked)
                    {
                        run search@ {
                            fleets.forEach {
                                if (it.name.lowercase().contains(filter) || it.id.lowercase().contains(filter) || it.tags.any { tag -> tag.lowercase().contains(filter) } || it.faction.id.lowercase().contains(filter) || it.faction.displayName.lowercase().contains(filter)) { entities.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                }
            }

            reset()
        }

        for (tpButton in teleportButtons)
        {
            if (tpButton.value.isChecked)
            {
                dialog.showTextPanel()
                dialog.showVisualPanel()
                dialog.dismiss()

                tpButton.value.isChecked = false

                Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet, JumpDestination(tpButton.key, ""), 0f)
            }
        }
    }

    override fun reset()
    {
        panel.removeComponent(entitiesElement)
        panel.removeComponent(entitiesPanel)
        panelsToOutline.clear()
        entitiesPanel()
    }

    override fun close()
    {
        panel.removeComponent(searchElement)

        panel.removeComponent(entitiesElement)
        panel.removeComponent(entitiesPanel)

        entitiesElement = null
        entitiesPanel = null
        entities.clear()
        filterButtons.clear()
        panelsToOutline.clear()
        searchBar = null
        searchElement = null
        previousString = "."
        start = true
        teleportButtons.clear()
    }
}

private class TooltipPreset(var text: String, var width: Float, vararg highlights: String) :
    TooltipMakerAPI.TooltipCreator
{

    var Highlights = highlights

    override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
        return false
    }

    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return width
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
        tooltip!!.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), *Highlights)
    }
}