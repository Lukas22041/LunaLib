package lunalib.lunaReflection;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

public class ReflectionClassLoader extends URLClassLoader
{
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ReflectionClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
