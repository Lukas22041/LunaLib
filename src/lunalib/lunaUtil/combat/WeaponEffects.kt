package lunalib.lunaUtil.combat

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import org.lwjgl.util.vector.Vector2f

object WeaponEffects
{
    //Somewhat based on part of Nicke's Homing script, but for easy use without requiring a mod to implement a script themself.
   /* @JvmStatic
    fun makeHoming(projectile: DamagingProjectileAPI, homingRange: Float, adjustSpeed: Float)
    {
        CombatHandler.homingProjectiles.add(HomingProjectileData(projectile, homingRange, adjustSpeed,
            listOf(ShipAPI.HullSize.FRIGATE, ShipAPI.HullSize.DESTROYER, ShipAPI.HullSize.CRUISER, ShipAPI.HullSize.CAPITAL_SHIP)))
    }

    @JvmStatic
    fun makeHoming(projectile: DamagingProjectileAPI, homingRange: Float, adjustSpeed: Float, targets: List<HullSize>)
    {
        CombatHandler.homingProjectiles.add(HomingProjectileData(projectile, homingRange, adjustSpeed, targets))
    }

    @JvmStatic
    fun addShockwave(projectile: DamagingProjectileAPI, range: Float, force: Float, duration: Float, effectProjectiles: Boolean)
    {
        CombatHandler.shockwaveProjectiles.add(
            ShockwaveProjectileData(projectile, range, force, duration, effectProjectiles, Global.getSettings().getSprite("luna", "shockwave"), false, duration, Vector2f(0f, 0f), 0f))
    }*/
}