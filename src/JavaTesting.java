import lunalib.lunaWrappers.LunaMemory;

public class JavaTesting
{
    LunaMemory TestString = new LunaMemory("VariableID");

    public JavaTesting()
    {
        TestString.set("A test String");
    }

    public void testMethod()
    {
        TestString.get();
    }
}
