package lunalib.lunaReflection

import LunaLibPlugin
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.campaign.CampaignEngine
import com.fs.starfarer.campaign.Faction
import com.fs.starfarer.loading.SpecStore
import com.fs.starfarer.loading.scripts.ScriptStore
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.net.URL
import java.net.URLClassLoader

object Test
{
    var strings: MutableList<String> = ArrayList()
}

//Class used to get around the reflection block.
//Based off on andylizi's approach to avoiding the reflection block in their mod Planet Search.
//https://fractalsoftworks.com/forum/index.php?topic=23229.0
class ReflectionUtils : EveryFrameScript
{
    override fun advance(amount: Float)
    {
        /* var script = Global.getSector().scripts.get(0)
         for (field in script.javaClass.declaredFields)
         {
             field.isAccessible = true
             if (field.type == Boolean::class.java)
             {
                 println("FieldTest: ${field.name} ${field.get(script)}")

                 field.set(script, true)
             }
         }
         Test.strings.add("Test2")
         for (string in Test.strings)
         {
             println(string)
         }*/
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }
}


