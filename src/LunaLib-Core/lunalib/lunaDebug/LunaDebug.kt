package lunalib.lunaDebug

import com.fs.starfarer.api.Global


object LunaDebug
{
    internal var snippets: MutableList<LunaSnippet> = ArrayList()

    @JvmStatic
    fun addSnippet(lunaSnippetClass: LunaSnippet)
    {
        snippets.add(lunaSnippetClass)
    }
}