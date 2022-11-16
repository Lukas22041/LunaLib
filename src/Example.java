import com.fs.starfarer.api.Global;
import lunalib.lunaUtil.LunaTimer;

public class Example
{

    //Creates a new timer, and sets its timestamp to the current Campaign time.
    LunaTimer timer = new LunaTimer();

    public void exampleMethod()
    {
        //Gets time in ingame days.
        Float daysSinceTimerStarted = timer.getDays();

        //Gets time in seconds, by converting days to the average time a day takes.
        Float secondsSinceTimerStarted = timer.getSeconds();

        //Assigns the current time as a new timestamp
        timer.reset();

        //Assigns a custom timestamp to the timer.
        timer.assign(Global.getSector().getClock().getTimestamp());
    }
}
