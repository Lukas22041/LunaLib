package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global

/**
 Utility class for keeping track of time changes within the CampaignLayer. Will not work in Combat.

 The Timer starts on creation of the Class.

 [LunaTimer on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaTimer)

```Java
 LunaTimer timer = new LunaTimer();

 public void exampleMethod()
 {
 //Gets time since creation in ingame days.
 Float daysSinceTimerStarted = timer.getDays();

 //Gets time in seconds, by converting days to the average amount of seconds an ingame day takes.
 Float secondsSinceTimerStarted = timer.getSeconds();

 //Assigns the current time as the new timestamp
 timer.reset();

 //Assigns a custom timestamp to the timer.
 timer.assign(Global.getSector().getClock().getTimestamp());
 }
 ```

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