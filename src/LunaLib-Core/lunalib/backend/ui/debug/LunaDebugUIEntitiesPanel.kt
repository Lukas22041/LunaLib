package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.FleetMemberValueGetter
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CustomCampaignEntity
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.components.base.*
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUITextField
import java.awt.Color

internal class LunaDebugUIEntitiesPanel : LunaDebugUIInterface {

    var parentPanel: CustomPanelAPI? = null
    var parentClass: LunaDebugUIMainPanel? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var entities: MutableList<Any> = ArrayList()

    var width = 0f
    var height = 0f

   // var filters = listOf("Current System", "Starsystems", "Stars", "Planets" ,"Custom Entities", "Fleets")
    companion object
    {
        var searchText = ""
        var filters = mutableMapOf("Current System Only" to false,"Starsystems" to true, "Stars" to true, "Planets" to true, "Custom Entities" to true, "Fleets" to true)
    }

    override fun getTab(): String {
        return "Entities"
    }

    override fun init(parentPanel: CustomPanelAPI, parentClass: LunaDebugUIMainPanel, panel: CustomPanelAPI) {
        this.parentPanel = parentPanel
        this.panel = panel
        this.parentClass = parentClass

        width = panel.position.width
        height = panel.position.height

        panelElement = panel.createUIElement(300f, height, false)
        panelElement!!.position.inTL(0f, 0f)

        panel.addUIElement(panelElement)

        var searchField = LunaUITextField("",0f, 0f, 250f, 30f,"Empty", "Debug", panel, panelElement!!).apply {
            onUpdate {

                var field = this as LunaUITextField<String>
                if (field.paragraph != null && isSelected() && searchText != field.paragraph!!.text)
                {
                    searchText = field!!.paragraph!!.text
                    searchForEntities()
                    createList()
                }
            }
        }
        searchField.paragraph!!.text = searchText
        searchField.value = searchText

        searchField.backgroundAlpha = 0.5f

        searchField.position!!.inTL(50f, 60f)

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
        //searchField.uiElement.addComponent(pan)
        searchField.lunaElement!!.addUIElement(pan)
        pan.position.inTL(0f, 0f)
        var para = pan.addPara("", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        if (searchText == "")
        {
            para.text = "Search"
        }
        para.position.inTL(searchField!!.position!!.width / 2 - para.computeTextWidth(para.text) / 2 , searchField!!.position!!.height / 2 - para.computeTextHeight(para.text) / 2)

        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "Search"
                para.position.inTL(searchField!!.position!!.width / 2 - para.computeTextWidth(para.text) / 2 , searchField!!.position!!.height / 2 - para.computeTextHeight(para.text) / 2)
            }
            else
            {
                para.text = ""
            }
            if (isHovering)
            {
                backgroundAlpha = 0.75f
            }
            else if (isSelected())
            {
                backgroundAlpha = 1f
            }
            else
            {
                backgroundAlpha = 0.5f
            }
        }


        panelElement!!.addSpacer(50f)

        for ((key, value) in filters)
        {
            var button = LunaUIButton(false, false,250f, 30f, "none", "debug", panel!!, panelElement!!).apply {
                this.buttonText!!.text = key
                this.buttonText!!.position.inTL(this.width / 2 - this.buttonText!!.computeTextWidth(this.buttonText!!.text) / 2, this.height / 2 - this.buttonText!!.computeTextHeight(this.buttonText!!.text) / 2)

                this.buttonText!!.setHighlightColor(Misc.getHighlightColor())
                //this.position!!.inTL(0f,0f)
                this.backgroundAlpha = 0.5f
                this.value = value

                onHoverEnter {
                    Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
                }


                panelElement!!.addSpacer(5f)
            }

            button.onClick {
                button.value = !button.value
                filters.put(key, button.value)

                searchForEntities()
                createList()
            }

            button.onUpdate {
                if (button.value)
                {
                    this.backgroundAlpha = 1f
                }
                else
                {
                    this.backgroundAlpha = 0.5f
                }
            }
        }

        searchForEntities()
        createList()
    }

    fun createList()
    {
        if (subpanel != null)
        {
            panel!!.removeComponent(subpanel)
        }

        subpanel = panel!!.createCustomPanel(width - 350, height, null)
        subpanel!!.position.inTL(350f, 0f)
        panel!!.addComponent(subpanel)
        subpanelElement = subpanel!!.createUIElement(width - 350, height , true)

        subpanelElement!!.position.inTL(0f, 0f)

        var spacing = 10f
        for (entity in entities)
        {
            var requiredSpacing = 0f
            var cardPanel = LunaUIPlaceholder(true, subpanel!!.position.width - 75, requiredSpacing, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            var descriptionElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.5f - 20, requiredSpacing, false)
            descriptionElement.position.inTL(0f, 0f)
           // cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var nameSuffix = when (entity)
            {
                is CustomCampaignEntity -> "Custom Entity"
                is PlanetAPI -> if (entity.isStar) "Star" else "Planet"
                is CampaignFleetAPI -> "Fleet"
                is StarSystemAPI -> "System"
                else -> ""
            }
            if (entity is LocationAPI)
            {
                var name = descriptionElement.addPara("${entity.name} ($nameSuffix)", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            }
            if (entity is SectorEntityToken)
            {
                var name = descriptionElement.addPara("${entity.name} ($nameSuffix)", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
            }

            //name.setHighlight("${data.fieldName}")
            descriptionElement.addSpacer(3f)

            var color = Misc.getBasePlayerColor()
            color = Color(color.red, color.green, color.blue, 230)

            var descriptionText = ""
            if (entity is SectorEntityToken)
            {

                descriptionText += "Id: ${entity.id}\n"
                descriptionText += "Tags: ${entity.tags}\n"
                if (entity is CustomCampaignEntity)
                {
                    descriptionText += "Type: ${entity.customEntityType}\n"
                }
                if (entity is PlanetAPI)
                {
                    descriptionText += "Type: ${entity.typeId}\n"
                }
                if (entity.faction != null)
                {
                    descriptionText += "Faction: ${entity.faction.id}\n"

                }
            }
            if (entity is StarSystemAPI)
            {
                descriptionText += "Id: ${entity.id}\n"
                descriptionText += "Tags: ${entity.tags}\n"
            }

            var description = descriptionElement.addPara(descriptionText,0f, color, color)

            /*var textWidth = description.computeTextWidth(description.text)
            var textHeight = description.computeTextHeight(description.text)
            var ratio = textWidth / description.position.width
            var extraSpace = textHeight * ratio
            var increase = extraSpace * 0.5f

            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + increase)
            subpanelElement!!.addSpacer(increase)*/

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.5f - 20, requiredSpacing, false)

            interactbleElement.position.inTL(subpanel!!.position.width * 0.5f, 0f)
           // cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            interactbleElement.addSpacer(5f)

            var space = 10f
            if (entity is PlanetAPI)
            {
                var sprite = LunaUISphere(entity.spec.texture, 40f, 40f, 40f, "", "Group", cardPanel.lunaElement!!, interactbleElement)
                sprite.position!!.inTL(interactbleElement.position!!.width / 2 - sprite.width / 2, space)


                if (entity.spec.cloudTexture != null)
                {
                    var sprite2 = LunaUISphere(entity.spec.cloudTexture, 40f, 40f, 40f, "", "Group", cardPanel.lunaElement!!, interactbleElement, isCloud = true, cloudColor = entity.spec.cloudColor)
                    sprite2.position!!.inTL(interactbleElement.position!!.width / 2 - sprite2.width / 2, space)
                }
               /* if (entity.spec.glowTexture != null)
                {
                    var sprite2 = LunaUISphere(entity.spec.glowTexture, 40f, 40f, 40f, "", "Group", cardPanel.lunaElement!!, interactbleElement, isCloud = true, cloudColor = entity.spec.glowColor)
                    sprite2.position!!.inTL(interactbleElement.position!!.width / 2 - sprite2.width / 2, space)
                }*/

                space += sprite.height * 2 + 10


            }
            else if (entity is StarSystemAPI)
            {
                if (entity.center is PlanetAPI)
                {
                    var center = entity.center as PlanetAPI
                    var sprite = LunaUISphere(center.spec.texture, 40f, 40f, 40f, "", "Group", cardPanel.lunaElement!!, interactbleElement)
                    sprite.position!!.inTL(interactbleElement.position!!.width / 2 - sprite.width / 2, space)

                    space += sprite.height * 2 + 10
                }
                else
                {
                    space += 40 * 2  + 10
                }
            }
            else if (entity is CampaignFleetAPI)
            {
                space += 40
            }
            else if (entity is SectorEntityToken)
            {
                if (entity.customEntitySpec.spriteName != null)
                {
                    var sprite = LunaUISprite(entity.customEntitySpec.spriteName, 80f, 80f, 40f, 40f, 300f, 600f, "", "Group", cardPanel.lunaElement!!, interactbleElement)
                    sprite.position!!.inTL(interactbleElement.position!!.width / 2 - sprite.textureWidth / 2, space)
                    space += sprite.textureHeight + 20f
                }
                else if (entity.customEntitySpec.iconName != null)
                {
                    var sprite = LunaUISprite(entity.customEntitySpec.iconName, 80f, 80f, 40f, 40f, 300f, 600f, "", "Group", cardPanel.lunaElement!!, interactbleElement)
                    sprite.position!!.inTL(interactbleElement.position!!.width / 2 - sprite.textureWidth / 2, space)
                    space += sprite.textureHeight + 20f
                }
                else
                {
                    space += 40 * 2  + 10
                }
            }


            var button = LunaUIButton(true, false,200f, 30f, entity, "Debug", cardPanel.lunaElement!!, interactbleElement)
            button.position!!.inTL(interactbleElement.position!!.width / 2 - button.position!!.width / 2, space)
            button.buttonText!!.text = "Teleport"
            button.buttonText!!.position.inTL(button.buttonText!!.position.width / 2 - button.buttonText!!.computeTextWidth(button.buttonText!!.text) / 2, button.buttonText!!.position.height - button.buttonText!!.computeTextHeight(button.buttonText!!.text) / 2)

            if (entity is SectorEntityToken)
            {
                button.onClick {
                    parentClass!!.close()
                    LunaUIBaseElement.selectedMap.clear()

                    var playerFleet = Global.getSector().playerFleet
                    var currentLocation = playerFleet.containingLocation
                    var targetSystem = entity.containingLocation

                    currentLocation.removeEntity(playerFleet)
                    targetSystem.addEntity(playerFleet)
                    Global.getSector().setCurrentLocation(targetSystem)
                    playerFleet.setLocation(entity.location.x, entity.location.y)

                  /*  Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet,
                        JumpPointAPI.JumpDestination(entity, ""), 0f)*/
                }
            }
            if (entity is StarSystemAPI)
            {
                button.onClick {
                    parentClass!!.close()
                    LunaUIBaseElement.selectedMap.clear()

                    var playerFleet = Global.getSector().playerFleet
                    var currentLocation = playerFleet.containingLocation
                    var targetSystem = entity

                    currentLocation.removeEntity(playerFleet)
                    targetSystem.addEntity(playerFleet)
                    Global.getSector().setCurrentLocation(targetSystem)
                    playerFleet.setLocation(entity.center.location.x, entity.center.location.y)

                  /*  Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet,
                        JumpPointAPI.JumpDestination(entity.center, ""), 0f)*/
                }
            }

            space += 40f

            //to create a small gap
            subpanelElement!!.addSpacer(5f)
            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height + space)

            spacing += 5f
            spacing += cardPanel.height

            subpanelElement!!.addSpacer(space)

        }

        subpanelElement!!.addSpacer(20f)
        subpanel!!.addUIElement(subpanelElement)
    }

    fun searchForEntities()
    {
        entities.clear()
        var capCount = 0

        var systems: MutableList<LocationAPI> = ArrayList()
        systems.addAll(Global.getSector().starSystems)
        systems.add(Global.getSector().hyperspace)

        var planets: MutableList<PlanetAPI> = ArrayList()
        var fleets: MutableList<CampaignFleetAPI> = ArrayList()
        var customEntities: MutableList<SectorEntityToken> = ArrayList()


        for (system in systems)
        {
            planets.addAll(system.planets)
            fleets.addAll(system.fleets)
            customEntities.addAll(system.customEntities)
        }

        var currentSystem = false
        var system: StarSystemAPI? = null

        var curSystemButton = filters.get("Current System Only")
        if (filters.get("Current System Only")!!)
        {
            val playerfleet = Global.getSector().playerFleet
            system = playerfleet.starSystem
            currentSystem = true
        }

        var searchInput = searchText.lowercase()


        //cant use fuzzy string matching, as this goes through way to many entities and starts becoming laggy quickly.
        if (filters.get("Starsystems")!!)
        {
            run search@ {
                systems.forEach {
                    if (currentSystem && it != system) return@forEach
                    if (searchText.isEmpty()) { entities.add(it); capCount++ }
                    else if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput) || it.tags.any { tag -> tag.lowercase().contains(searchInput) }) { entities.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }

        if (filters.get("Stars")!!)
        {
            run search@ {
                planets.forEach {
                    if (currentSystem && it.starSystem != system) return@forEach
                    if (!it.isStar) return@forEach
                    if (searchText.isEmpty()) { entities.add(it); capCount++ }
                    else if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput) || it.tags.any { tag -> tag.lowercase().contains(searchInput) }) { entities.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }

        if (filters.get("Planets")!!)
        {
            run search@ {
                planets.forEach {
                    if (currentSystem && it.starSystem != system) return@forEach
                    if (it.isStar) return@forEach
                    if (searchText.isEmpty()) { entities.add(it); capCount++ }
                    else if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput) || it.tags.any { tag -> tag.lowercase().contains(searchInput) } || it.faction.id.lowercase().contains(searchInput) || it.faction.displayName.lowercase().contains(searchInput)) { entities.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }

        if (filters.get("Custom Entities")!!)
        {
            run search@ {
                customEntities.forEach {
                    if (currentSystem && it.starSystem != system) return@forEach
                    if (it.tags.contains("orbital_junk")) return@forEach
                    if (searchText.isEmpty()) { entities.add(it); capCount++ }
                    else if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput) || it.tags.any { tag -> tag.lowercase().contains(searchInput) } || it.faction.id.lowercase().contains(searchInput) || it.faction.displayName.lowercase().contains(searchInput)) { entities.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }

        if (filters.get("Fleets")!!)
        {
            run search@ {
                fleets.forEach {
                    if (currentSystem && it.starSystem != system) return@forEach
                    if (searchText.isEmpty()) { entities.add(it); capCount++ }
                    else if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput) || it.tags.any { tag -> tag.lowercase().contains(searchInput) } || it.faction.id.lowercase().contains(searchInput) || it.faction.displayName.lowercase().contains(searchInput)) { entities.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }

    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>?) {

    }

    override fun buttonPressed(buttonId: Any?) {

    }
}