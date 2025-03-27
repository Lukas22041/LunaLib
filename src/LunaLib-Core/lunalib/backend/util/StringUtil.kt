package lunalib.backend.util

import com.fs.starfarer.api.Global

//Internal Lunalib use only
fun String.getLunaString() : String
{
    return Global.getSettings().getString("lunalib", this)
}

