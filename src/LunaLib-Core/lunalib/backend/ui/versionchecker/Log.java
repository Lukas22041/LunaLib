package lunalib.backend.ui.versionchecker;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

// Minor utility class to ensure all logging is done under the same class
class Log
{
    private static final Logger _Log = Logger.getLogger(VersionChecker.class);

    static void setLevel(Level level)
    {
        _Log.setLevel(level);
    }

    static void info(Object message)
    {
        _Log.info(message);
    }

    static void info(Object message, Throwable ex)
    {
        _Log.info(message, ex);
    }

    static void debug(Object message)
    {
        _Log.debug(message);
    }

    static void debug(Object message, Throwable ex)
    {
        _Log.debug(message, ex);
    }

    static void warn(Object message)
    {
        _Log.warn(message);
    }

    static void warn(Object message, Throwable ex)
    {
        _Log.warn(message, ex);
    }

    static void error(Object message)
    {
        _Log.warn(message);
    }

    static void error(Object message, Throwable ex)
    {
        _Log.warn(message, ex);
    }

    static void fatal(Object message)
    {
        _Log.fatal(message);
    }

    static void fatal(Object message, Throwable ex)
    {
        _Log.fatal(message, ex);
    }

    private Log()
    {
    }
}
