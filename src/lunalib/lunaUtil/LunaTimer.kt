package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import lunalib.lunaUtil.LunaMisc
import org.lwjgl.util.vector.Vector2f
import java.sql.Timestamp

/**
 *Utility class for keeping track of time changes within the CampaignLayer. Will not work in Combat.
 *The Timer starts on creation of the Class.
 */
class LunaTimer()
{
    private var timestamp: Long = 0

    init {
        timestamp = Global.getSector().clock.timestamp
    }

    /**
     *Gets the time passed since the creation of the timestamp in ingame days.
     */
    fun getDays() : Float
    {
        return  Global.getSector().clock.getElapsedDaysSince(timestamp)
    }

    /**
     *Gets the time passed since the creation of the timestamp by converting ingame days to the amount of seconds an ingame day takes.
     */
    fun getSeconds() : Float
    {
        return Global.getSector().clock.convertToSeconds(getDays())
    }

    /**
     *Resets the timestamp to the current time.
     */
    fun reset()
    {
        timestamp = Global.getSector().clock.timestamp
    }

    /**
     *Replaces the current timestamp with the provided one.
     * @param timestamp The timestamp to assign
     */
    fun assign(timestamp: Long)
    {
        this.timestamp = timestamp
    }
}