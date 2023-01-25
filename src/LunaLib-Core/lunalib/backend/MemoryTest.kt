package lunalib.backend

import com.fs.starfarer.api.Global
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MemoryTest<T>(private var key: String? = null, var default: T? = null) : ReadWriteProperty<T?, T> {

    override operator fun getValue(thisRef: T?, property: KProperty<*>): T {
        return Global.getSector().memoryWithoutUpdate.get(key ?: ("$" + property.name)) as T
    }

    override operator fun setValue(thisRef: T?, property: KProperty<*>, value: T) {
        Global.getSector().memoryWithoutUpdate.set(key ?: ("$" + property.name), value)
    }
}

