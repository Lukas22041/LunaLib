package lunalib.backend.util

import com.fs.starfarer.api.Global

//Internal Lunalib use only
internal fun String.getLunaString() : String
{
    return Global.getSettings().getString("lunalib", this)
}

