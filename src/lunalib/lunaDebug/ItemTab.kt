package lunalib.lunaDebug

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.fleet.FleetMemberType
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TextFieldAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.LunaSettings

data class GiveData(var item: Any, var amount: Int)

internal class ItemTab(panel: CustomPanelAPI, dialog: InteractionDialogAPI) : DebugTab(panel, dialog)
{
    override var panelName = "Items"

    var start = true

    var searchElement: TooltipMakerAPI? = null
    var searchBar: TextFieldAPI? = null

    var filterButtons: MutableMap<String, ButtonAPI> = HashMap()
    var checkedFilterButtons: MutableMap<ButtonAPI, Boolean> = HashMap()

    var itemsPanel: CustomPanelAPI? = null
    var itemsElement: TooltipMakerAPI? = null
    var items: MutableList<Any> = ArrayList()

    var previousString = "."

    var giveButtons: MutableMap<GiveData, ButtonAPI> = HashMap()

    var filters = listOf("Ships", "Weapons", "Fighters", "Hullmods", "Commodities")
    companion object
    {
        var lastString = ""
        var checkedFilters =
            mutableMapOf("Ships" to true, "Weapons" to true, "Fighters" to true, "Hullmods" to true, "Commodities" to true)
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

        var spacingPara = searchElement!!.addPara("Search for Items (Name/Id)", 0f)
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
        itemsPanel = panel.createCustomPanel(pWidth * 0.7f, pHeight * 0.8f, null)
        itemsPanel!!.position.setLocation(0f, 0f).inTL(pWidth * 0.27f, pHeight * 0.15f)

        itemsElement = itemsPanel!!.createUIElement(pWidth * 0.7f, pHeight * 0.8f, true)

        var spacing = 0f

        for (entity in items)
        {
            val spacer: ButtonAPI = itemsElement!!.addAreaCheckbox("", null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                itemsPanel!!.position.width,
                pHeight * 0.2f,
                0f)

            spacer.isEnabled = false
            spacer.position.inTL(0f, spacing)

            addItemToPanel(entity, spacing, spacer)

            spacing += pHeight * 0.2f
        }

        itemsPanel!!.addUIElement(itemsElement)
        panel.addComponent(itemsPanel)
        panelsToOutline.add(itemsPanel!!)
    }

    private fun addItemToPanel(item: Any, spacing: Float, spacer: ButtonAPI)
    {
        var offset = pHeight * 0.2f
        when (item)
        {
            is ShipHullSpecAPI ->
            {
                var name = itemsElement!!.addPara("${item.hullName} (Ship)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                itemsElement!!.addPara("Id: ${item.hullId}", Misc.getBasePlayerColor(), 0f)

                itemsElement!!.addPara("", 0f)


                itemsElement!!.addImage(item.spriteName, 64f, 64f, 0f)

                val give1: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 1x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                give1.position.inTL(pWidth * 0.4f,(spacing + offset / 5) - name.position.height / 2)

                val give5: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 5x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                val give10: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 10x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                giveButtons.put(GiveData(item, 1), give1)
                giveButtons.put(GiveData(item, 5), give5)
                giveButtons.put(GiveData(item, 10), give10)
            }

            is WeaponSpecAPI ->
            {
                var name = itemsElement!!.addPara("${item.weaponName} (Weapon)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                itemsElement!!.addPara("Id: ${item.weaponId}", Misc.getBasePlayerColor(), 0f)

                itemsElement!!.addPara("", 0f)

                itemsElement!!.addImage(item.turretSpriteName, 64f, 64f, 0f)

                val give1: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 1x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                give1.position.inTL(pWidth * 0.4f,(spacing + offset / 5) - name.position.height / 2)

                val give5: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 10x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                val give10: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 100x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                giveButtons.put(GiveData(item, 1), give1)
                giveButtons.put(GiveData(item, 10), give5)
                giveButtons.put(GiveData(item, 100), give10)
            }

            is HullModSpecAPI ->
            {
                var name = itemsElement!!.addPara("${item.displayName} (Hullmod)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                itemsElement!!.addPara("Id: ${item.id}", Misc.getBasePlayerColor(), 0f)

                itemsElement!!.addPara("", 0f)

                itemsElement!!.addImage(item.spriteName, 64f, 64f, 0f)

                val give1: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 1x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                give1.position.inTL(pWidth * 0.4f,(spacing + offset / 5) - name.position.height / 2)

                val give5: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 5x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                val give10: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 10x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                giveButtons.put(GiveData(item, 1), give1)
                giveButtons.put(GiveData(item, 5), give5)
                giveButtons.put(GiveData(item, 10), give10)
            }

            is FighterWingSpecAPI ->
            {
                var name = itemsElement!!.addPara("${item.wingName} (Fighter)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                itemsElement!!.addPara("Id: ${item.id}", Misc.getBasePlayerColor(), 0f)

                itemsElement!!.addPara("", 0f)

                val give1: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 1x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                give1.position.inTL(pWidth * 0.4f,(spacing + offset / 5) - name.position.height / 2)

                val give5: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 5x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                val give10: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 10x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                giveButtons.put(GiveData(item, 1), give1)
                giveButtons.put(GiveData(item, 5), give5)
                giveButtons.put(GiveData(item, 10), give10)
            }

            is CommoditySpecAPI ->
            {
                var name = itemsElement!!.addPara("${item.name} (Commodity)", 0f)
                name.position.inTL(pWidth * 0.010f,(spacing + offset / 2) - name.position.height * 4)

                itemsElement!!.addPara("Id: ${item.id}", Misc.getBasePlayerColor(), 0f)

                itemsElement!!.addPara("", 0f)

                itemsElement!!.addImage(item.iconName, 64f, 64f, 0f)

                val give1: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 10x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                give1.position.inTL(pWidth * 0.4f,(spacing + offset / 5) - name.position.height / 2)

                val give5: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 100x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                val give10: ButtonAPI = itemsElement!!.addAreaCheckbox("Give 1000x", null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    panel.position.width * 0.2f,
                    pHeight * 0.05f,
                    0f)

                giveButtons.put(GiveData(item, 10), give1)
                giveButtons.put(GiveData(item, 100), give5)
                giveButtons.put(GiveData(item, 1000), give10)
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
            items.clear()

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
                    var filter = filt.trim()

                    if (button.key == "Ships" && button.value.isChecked)
                    {
                        run search@ {
                            Global.getSettings().allShipHullSpecs.forEach {
                                if (it.hullSize == ShipAPI.HullSize.FIGHTER) return@forEach
                                if (it.hullName.contains(filter) || it.hullId.contains(filter)) { items.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                    if (button.key == "Weapons" && button.value.isChecked)
                    {
                        run search@ {
                            Global.getSettings().allWeaponSpecs.forEach {
                                if (it.weaponName.contains(filter) || it.weaponId.contains(filter)) { items.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                    if (button.key == "Hullmods" && button.value.isChecked)
                    {
                        run search@ {
                            Global.getSettings().allHullModSpecs.forEach {
                                if (it.displayName.contains(filter) || it.id.contains(filter)) { items.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                    if (button.key == "Fighters" && button.value.isChecked)
                    {
                        run search@ {
                            Global.getSettings().allFighterWingSpecs.forEach {
                                if (it.wingName.contains(filter) || it.id.contains(filter)) { items.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                    if (button.key == "Commodities" && button.value.isChecked)
                    {
                        run search@ {
                            Global.getSettings().allCommoditySpecs.forEach {
                                if (it.name.contains(filter) || it.id.contains(filter)) { items.add(it); capCount++ }
                                if (capCount > cap) return@search
                            }
                        }
                    }
                }
            }

            reset()
        }

        for (button in giveButtons)
        {
            if (button.value.isChecked)
            {
                var data = button.key
                button.value.isChecked = false
                when (data.item)
                {
                    is CommoditySpecAPI ->
                    {
                        Global.getSector().playerFleet.cargo.addCommodity((data.item as CommoditySpecAPI).id, data.amount.toFloat())
                    }
                    is ShipHullSpecAPI ->
                    {
                        for (i in 0 until data.amount)
                        {
                            var member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, (data.item as ShipHullSpecAPI).baseHullId + "_Hull");
                            Global.getSector().playerFleet.fleetData.addFleetMember(member)
                        }
                    }
                    is HullModSpecAPI ->
                    {
                        Global.getSector().playerFleet.cargo.addHullmods((data.item as HullModSpecAPI).id, data.amount)
                    }
                    is WeaponSpecAPI ->
                    {
                        Global.getSector().playerFleet.cargo.addWeapons((data.item as WeaponSpecAPI).weaponId, data.amount)
                    }
                    is FighterWingSpecAPI ->
                    {
                        Global.getSector().playerFleet.cargo.addFighters((data.item as FighterWingSpecAPI).id, data.amount)
                    }
                }
            }
        }
    }

    override fun reset()
    {
        panel.removeComponent(itemsElement)
        panel.removeComponent(itemsPanel)
        panelsToOutline.clear()
        entitiesPanel()
    }

    override fun close()
    {
        panel.removeComponent(searchElement)

        panel.removeComponent(itemsElement)
        panel.removeComponent(itemsPanel)

        itemsElement = null
        itemsPanel = null
        items.clear()
        filterButtons.clear()
        panelsToOutline.clear()
        searchBar = null
        searchElement = null
        previousString = "."
        start = true
        giveButtons.clear()
    }
}