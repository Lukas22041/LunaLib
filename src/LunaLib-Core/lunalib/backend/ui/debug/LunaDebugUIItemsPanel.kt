package lunalib.backend.ui.debug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.fleet.FleetMemberType
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.components.base.LunaUIButton
import lunalib.backend.ui.components.base.LunaUIPlaceholder
import lunalib.backend.ui.components.base.LunaUISlider
import lunalib.backend.ui.components.base.LunaUITextField
import java.awt.Color

internal class LunaDebugUIItemsPanel : LunaDebugUIInterface {

    var parentPanel: CustomPanelAPI? = null
    var parentClass: LunaDebugUIMainPanel? = null

    var panel: CustomPanelAPI? = null
    var panelElement: TooltipMakerAPI? = null

    var subpanel: CustomPanelAPI? = null
    var subpanelElement: TooltipMakerAPI? = null

    var items: MutableList<Any> = ArrayList()

    var width = 0f
    var height = 0f

    // var filters = listOf("Current System", "Starsystems", "Stars", "Planets" ,"Custom Entities", "Fleets")
    companion object
    {
        var searchText = ""
        var filters = mutableMapOf("Ships" to true, "Weapons" to true, "Fighters" to true, "Hullmods" to true, "Commodities" to true)
    }

    override fun getTab(): String {
        return "Items"
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
                if (field.paragraph != null && isSelected() && searchText != field.paragraph!!.text.replace("_", ""))
                {
                    searchText = field!!.paragraph!!.text.replace("_", "")
                    searchForItems()
                    createList()
                }
            }
        }
        searchField.paragraph!!.text = searchText
        searchField.value = searchText

        searchField.backgroundAlpha = 0.5f

        searchField.position!!.inTL(50f, 60f)

        var pan = searchField.lunaElement!!.createUIElement(searchField.position!!.width, searchField.position!!.height, false)
        searchField.uiElement.addComponent(pan)
        searchField.lunaElement!!.addUIElement(pan)
        pan.position.inTL(0f, 0f)
        var para = pan.addPara("", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        if (searchText == "")
        {
            para.text = "Search"
        }
        para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)

        searchField.onHoverEnter {
            Global.getSoundPlayer().playUISound("ui_number_scrolling", 1f, 0.8f)
        }
        searchField.onUpdate {
            var button = this as LunaUITextField<String>
            button.resetParagraphIfEmpty = false
            if (button.paragraph!!.text == "" && !button.isSelected())
            {
                para.text = "Search"
                para.position.inTL(para.position.width / 2 - para.computeTextWidth(para.text) / 2 , para.position.height  - para.computeTextHeight(para.text) / 2)
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
            var button = LunaUIButton(false, false,250f, 30f, "none", "Debug", panel!!, panelElement!!).apply {
                this.buttonText!!.text = key
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

                searchForItems()
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

        searchForItems()
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
        for (item in items)
        {
            var requiredSpacing = 100f
            var cardPanel = LunaUIPlaceholder(true, subpanel!!.position.width - 75, requiredSpacing, "empty", "none", subpanel!!, subpanelElement!!)
            cardPanel.position!!.inTL(10f, spacing)

            var descriptionElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.6f - 20, requiredSpacing, false)
            descriptionElement.position.inTL(0f, 0f)
            cardPanel.uiElement.addComponent(descriptionElement)
            cardPanel.lunaElement!!.addUIElement(descriptionElement)

            descriptionElement.addSpacer(5f)

            var name = when (item)
            {
                is ShipHullSpecAPI -> "${item.hullName} (Ship)"
                is WeaponSpecAPI -> "${item.weaponName} (Weapon)"
                is HullModSpecAPI -> "${item.displayName} (Hullmod)"
                is FighterWingSpecAPI -> "${item.wingName} (Fighter)"
                is CommoditySpecAPI -> "${item.name} (Commodity)"
                else -> ""
            }

            var namePara = descriptionElement.addPara("$name", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            descriptionElement.addSpacer(3f)

            var color = Misc.getBasePlayerColor()
            color = Color(color.red, color.green, color.blue, 230)

            var descriptionText = ""

            when (item)
            {
                is ShipHullSpecAPI -> {
                    descriptionText += "Id: ${item.hullId}\n"
                    descriptionText += "Hullsize: ${item.hullSize}\n"
                    descriptionText += "Shipsystem: ${item.shipSystemId}\n"
                    descriptionText += "Hullmods: ${item.builtInMods}\n"
                }
                is WeaponSpecAPI -> {
                    descriptionText += "Id: ${item.weaponId}\n"
                    descriptionText += "Size: ${item.size}\n"
                    descriptionText += "Type: ${item.type}\n"
                }
                is HullModSpecAPI -> {
                    descriptionText += "Id: ${item.id}\n"
                    descriptionText += "Category: ${item.uiTags}\n"
                }
                is FighterWingSpecAPI -> {
                    descriptionText += "Id: ${item.id}\n"
                    descriptionText += "Variant Id: ${item.variantId}\n"
                }
                is CommoditySpecAPI -> {
                    descriptionText += "Id: ${item.id}\n"
                    descriptionText += "Base Value: ${item.basePrice}\n"
                }
            }

            var description = descriptionElement.addPara(descriptionText,0f, color, color)

            var textWidth = description.computeTextWidth(description.text)
            var textHeight = description.computeTextHeight(description.text)
            var ratio = textWidth / description.position.width
            var extraSpace = textHeight * ratio
            var increase = extraSpace * 0.5f

            cardPanel.position!!.setSize(cardPanel.position!!.width, cardPanel.position!!.height  + increase)
            subpanelElement!!.addSpacer(increase)

            var interactbleElement = cardPanel.lunaElement!!.createUIElement(subpanel!!.position.width * 0.4f - 20, requiredSpacing, false)

            interactbleElement.position.inTL(10f + subpanel!!.position.width * 0.6f, 0f)
            cardPanel.uiElement.addComponent(interactbleElement)
            cardPanel.lunaElement!!.addUIElement(interactbleElement)

            interactbleElement.addSpacer(5f)


            var max = when (item) {
                is ShipHullSpecAPI -> 10f
                is WeaponSpecAPI -> 10f
                is HullModSpecAPI -> 10f
                is FighterWingSpecAPI -> 10f
                is CommoditySpecAPI -> 1000f
                else -> 10f
            }

            var textField = LunaUISlider(1, 1f,  max, 200f, 20f, "none", "Debug", cardPanel.lunaElement!!, interactbleElement)
            textField.position!!.inTL(0f, cardPanel.height / 2 - textField.height / 2 - 15f)

            var button = LunaUIButton(true, false,200f, 30f, item, "Debug", cardPanel.lunaElement!!, interactbleElement)
            button.buttonText!!.text = "Cheat in ${textField.value}"
            button.buttonText!!.position.inTL(button.buttonText!!.position.width / 2 - button.buttonText!!.computeTextWidth(button.buttonText!!.text) / 2, button.buttonText!!.position.height - button.buttonText!!.computeTextHeight(button.buttonText!!.text) / 2)

            button.onUpdate {
                if (button.buttonText!!.text != "Cheat in ${textField.value}")
                {
                    button.buttonText!!.text = "Cheat in ${textField.value}"
                    button.buttonText!!.position.inTL(button.buttonText!!.position.width / 2 - button.buttonText!!.computeTextWidth(button.buttonText!!.text) / 2, button.buttonText!!.position.height - button.buttonText!!.computeTextHeight(button.buttonText!!.text) / 2)
                }
            }

            button.onClick {
                when (item)
                {
                    is ShipHullSpecAPI -> {
                        for (i in 0 until textField.value)
                        {
                            var member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, item.baseHullId + "_Hull");
                            Global.getSector().playerFleet.fleetData.addFleetMember(member)
                        }
                    }
                    is WeaponSpecAPI -> {
                        Global.getSector().playerFleet.cargo.addWeapons(item.weaponId, textField.value)
                    }
                    is HullModSpecAPI -> {
                        Global.getSector().playerFleet.cargo.addHullmods(item.id, textField.value)
                    }
                    is FighterWingSpecAPI -> {
                        Global.getSector().playerFleet.cargo.addFighters(item.id, textField.value)
                    }
                    is CommoditySpecAPI -> {
                        Global.getSector().playerFleet.cargo.addCommodity(item.id, textField.value.toFloat())
                    }
                }
            }

            spacing += cardPanel.height

            //to create a small gap
            spacing += 5f
            subpanelElement!!.addSpacer(5f)

        }
        subpanelElement!!.addSpacer(20f)
        subpanel!!.addUIElement(subpanelElement)
    }

    fun searchForItems()
    {
        items.clear()

        var searchInput = searchText.lowercase()

        var capCount = 0

        if (filters.get("Ships")!!)
        {
            run search@ {
                Global.getSettings().allShipHullSpecs.forEach {
                    if (it.hullSize == ShipAPI.HullSize.FIGHTER) return@forEach
                    if (it.hullName.lowercase().contains(searchInput) || it.hullId.lowercase().contains(searchInput)) { items.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }
        if (filters.get("Weapons")!!)
        {
            run search@ {
                Global.getSettings().allWeaponSpecs.forEach {
                    if (it.weaponName.lowercase().contains(searchInput) || it.weaponId.lowercase().contains(searchInput)) { items.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }
        if (filters.get("Hullmods")!!)
        {
            run search@ {
                Global.getSettings().allHullModSpecs.forEach {
                    if (it.displayName.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput)) { items.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }
        if (filters.get("Fighters")!!)
        {
            run search@ {
                Global.getSettings().allFighterWingSpecs.forEach {
                    if (it.wingName.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput)) { items.add(it); capCount++ }
                    if (capCount > LoadedSettings.debugEntryCap!!) return@search
                }
            }
        }
        if (filters.get("Commodities")!!)
        {
            run search@ {
                Global.getSettings().allCommoditySpecs.forEach {
                    if (it.name.lowercase().contains(searchInput) || it.id.lowercase().contains(searchInput)) { items.add(it); capCount++ }
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
}