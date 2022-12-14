package lunalib.lunaUtil.combat

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.LunaSettings
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.VectorUtils
import org.lazywizard.lazylib.combat.CombatUtils
import org.lazywizard.lazylib.ui.LazyFont
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector2f


data class HomingProjectileData(var projectile: DamagingProjectileAPI?, val range: Float, var speed: Float, var targets: List<HullSize>)
data class ShockwaveProjectileData(var projectile: DamagingProjectileAPI?, val range: Float, var force: Float, var maxDuration: Float, var effectProjectiles: Boolean
                                    , var sprite: SpriteAPI, var didDamage: Boolean, var duration: Float, var hitLoc: Vector2f, var distance: Float)

class CombatHandler : EveryFrameCombatPlugin
{
    companion object
    {
        var homingProjectiles: MutableList<HomingProjectileData> = ArrayList()
        var shockwaveProjectiles: MutableList<ShockwaveProjectileData> = ArrayList()
    }
    var engine: CombatEngineAPI? = null
    private var toDraw: LazyFont.DrawableString? = null
    var settingsKeybind = ""
    override fun init(engine: CombatEngineAPI?)
    {
        this.engine = engine
        homingProjectiles.clear()
        shockwaveProjectiles.clear()

        val font: LazyFont
        font = LazyFont.loadFont("graphics/fonts/insignia15LTaa.fnt")

        toDraw = font.createText("LunaLib\n", Misc.getHighlightColor(), 15f)
        toDraw!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or under New Game -> Sector Configuration -> Mod Settings ", Misc.getBasePlayerColor())
        toDraw!!.maxWidth = 400f
    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?)
    {
        var projToRemove: MutableList<ShockwaveProjectileData> = ArrayList()
        if (engine == null) return
        if (engine!!.isPaused) return

        for (shockwave in shockwaveProjectiles)
        {
            if (shockwave.projectile == null) continue
            var proj = shockwave.projectile
            if (proj!!.didDamage())
            {
                shockwave.duration = shockwave.duration - ((1 * amount) / engine!!.timeMult.mult )
                var iter = engine!!.allObjectGrid.getCheckIterator(proj!!.location, shockwave.range, shockwave.range)
                while (iter.hasNext())
                {
                    var target = iter.next()

                    if (!shockwave.effectProjectiles && target !is MissileAPI && target is DamagingProjectileAPI)
                    {

                    }
                    else if (target is CombatEntityAPI)
                    {

                        var distance = MathUtils.getDistance(proj, target)
                        var force = shockwave.force
                        if (target is DamagingProjectileAPI)
                        {
                            force /= 20f
                        }

                        var angle = VectorUtils.getAngle(proj.location, target.location)
                        CombatUtils.applyForce(target, angle, force)
                    }
                }
            }

            if (shockwave.duration <= 0)
            {
                projToRemove.add(shockwave)
            }
        }
        shockwaveProjectiles.removeAll(projToRemove)

        homingProjectiles = homingProjectiles.filterNotNull() as MutableList<HomingProjectileData>
        for (homing in homingProjectiles)
        {
            if (homing.projectile == null) continue
            var proj = homing.projectile
            var targets: MutableList<ShipAPI> = ArrayList()
            var iter = engine!!.shipGrid.getCheckIterator(proj!!.location, homing.range, homing.range)
            while (iter.hasNext())
            {
                var target = iter.next() as ShipAPI
                if (target.owner == 1 && homing.targets.contains(target.hullSize)) targets.add(target)
            }
            var target: ShipAPI? = null
            var distance = 100000f
            for (tar in targets)
            {
                var dist = MathUtils.getDistance(proj, tar)
                if (dist < distance)
                {
                    target = tar
                    distance = dist
                }
            }
            if (target == null) continue

            var facing = VectorUtils.getAngle(proj.location, target.location)

            facing = MathUtils.clamp(facing, proj.facing - homing.speed, proj.facing + homing.speed)
            proj.facing = facing
            proj.velocity.x = MathUtils.getPoint(Vector2f(0f, 0f), proj.getVelocity().length(), facing).x
            proj.velocity.y = MathUtils.getPoint(Vector2f(0f, 0f), proj.getVelocity().length(), facing).y
        }
    }

    override fun renderInWorldCoords(viewport: ViewportAPI?)
    {
        for (shockwave in shockwaveProjectiles)
        {
            var proj = shockwave.projectile
            var level = shockwave.duration / shockwave.maxDuration

            if (proj!!.didDamage())
            {
                if (!shockwave.didDamage)
                {
                    shockwave.sprite.angle = MathUtils.getRandomNumberInRange(0f, 360f)
                }
                shockwave.hitLoc = proj.location
                shockwave.didDamage = true
            }

            if (shockwave.didDamage)
            {
                if (engine != null && !engine!!.isPaused)
                {
                    shockwave.distance = shockwave.distance + ((1000  * engine!!.elapsedInLastFrame) * level)
                }

                shockwave.sprite.alphaMult = level / 3
                shockwave.sprite.setSize(shockwave.distance, shockwave.distance)
                shockwave.sprite.renderAtCenter(shockwave.hitLoc.x, shockwave.hitLoc.y)

                shockwave.sprite.setSize(shockwave.distance * 0.75f, shockwave.distance * 0.75f)
                shockwave.sprite.renderAtCenter(shockwave.hitLoc.x, shockwave.hitLoc.y)

                shockwave.sprite.setSize(shockwave.distance / 2, shockwave.distance / 2)
                shockwave.sprite.renderAtCenter(shockwave.hitLoc.x, shockwave.hitLoc.y)
            }
        }

    }

    override fun renderInUICoords(viewport: ViewportAPI?) {
        if (Global.getCurrentState() == GameState.TITLE && toDraw != null)
        {
            settingsKeybind = Keyboard.getKeyName(LunaSettings.getInt("lunalib", "luna_SettingsKeybind")!!)

            var location = "New Game -> Ship Selection -> Difficulty -> Mod Settings."
            if (Global.getSettings().modManager.isModEnabled("nexerelin"))
            {
                 location = "New Game -> Sector Configuration -> Mod Settings."
            }
            toDraw!!.draw(100f,100f);
            toDraw!!.text = "LunaLib "
            toDraw!!.append(" \n", Misc.getBasePlayerColor())
            toDraw!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or at $location", Misc.getBasePlayerColor())

        }
    }
}