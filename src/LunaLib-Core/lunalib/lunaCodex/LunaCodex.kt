package lunalib.lunaCodex

import com.fs.starfarer.api.impl.codex.CodexDataV2
import com.fs.starfarer.api.impl.codex.CodexEntryPlugin
import com.fs.starfarer.api.impl.codex.CodexEntryV2

object LunaCodex {

    @JvmStatic
    var CODEX_CAT_MODS = "lunalib_mods"

    @JvmStatic
    fun getModsCategory() : CodexEntryPlugin {
        return CodexDataV2.getEntry(LunaCodex.CODEX_CAT_MODS)
    }

    @JvmStatic
    fun getModEntryId(modId: String) : String {
        return "codex_mods_$modId"
    }

}