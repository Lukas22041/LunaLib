package lunalib.lunaDelegates

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.rpg.Person
import kotlin.reflect.KProperty

/**
* A delegate that causes setting and getting from that variable to interact with Starsectors MemoryAPI instead.
*
* [key] sets the memory key (No $). If it is null, it sets the key to the variables name.
 *
* [default] is returned if the memory entry is null
 *
* [targetMemory] allows to set what memory it is saved to, if it is null, it's saved to the Sectors MemoryAPI.
 *
 * Example:
 *
```java
//Creates the variable with the delegate. The Variable has to be nullable.
var testMemoryEntry: String? by LunaMemory()

//Instead of "Global.getSector().memoryWithoutUpdate.set("\$testMemoryEntry", "Test thing")"
testMemoryEntry = "Test thing"

//Instead of "Global.getSector().memoryWithoutUpdate.get("\$testMemoryEntry")"
var test = testMemoryEntry
```
*/

class LunaMemory<T>(val key: String? = null, val default: T? = null, val targetMemory : MemoryAPI? = null) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (targetMemory != null)
        {
            return targetMemory.get("\$${key ?: property.name}") as T ?: default
        }
        else
        {
            return Global.getSector().memoryWithoutUpdate.get("\$${key ?: property.name}") as T ?: default
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (targetMemory != null)
        {
            targetMemory.set("\$${key ?: property.name}", value)
        }
        else
        {
            Global.getSector().memoryWithoutUpdate.set("\$${key ?: property.name}", value)
        }
    }

}

