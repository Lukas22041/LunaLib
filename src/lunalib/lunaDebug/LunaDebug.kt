package lunalib.lunaDebug

import com.fs.starfarer.api.Global


object LunaDebug
{
    internal var snippets: MutableList<lunalib.lunaDebug.LunaSnippet> = ArrayList()

    @JvmStatic
    fun addSnippet(lunaSnippetClass: lunalib.lunaDebug.LunaSnippet)
    {
        snippets.add(lunaSnippetClass)
    }
}