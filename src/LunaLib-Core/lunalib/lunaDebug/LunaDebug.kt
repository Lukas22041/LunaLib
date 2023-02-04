package lunalib.lunaDebug


object LunaDebug
{
    internal var snippets: MutableList<LunaSnippet> = ArrayList()

    @JvmStatic
    fun addSnippet(lunaSnippetClass: LunaSnippet)
    {
        snippets.add(lunaSnippetClass)
    }
}