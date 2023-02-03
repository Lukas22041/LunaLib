package lunalib.lunaDebug

import lunalib.lunaDebug.snippets.ExampleSnippet


object LunaDebug
{
    internal var snippets: MutableList<Class<*>> = ArrayList()

    @JvmStatic
    fun <T : LunaSnippet> addSnippet(lunaSnippetClass: Class<T>)
    {
        snippets.add(lunaSnippetClass)
    }
}