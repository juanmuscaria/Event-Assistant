import com.juanmuscaria.event_assistant.ReflectionAssistant;
import com.juanmuscaria.event_assistant.utils.ClassloaderHacks;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Bootstrap;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HackyMinecraftEnv {
    final URLClassLoader ucl;
    final LaunchClassLoader classLoader;
    final ReflectionAssistant.MethodInvoker defineClass = ReflectionAssistant.getMethod(ClassLoader.class, "defineClass", String.class, byte[].class, int.class, int.class);
    final ClassloaderHacks classloaderCallable;

    public HackyMinecraftEnv() {
        this(false);
    }

    public HackyMinecraftEnv(boolean removeBukkit) {
        ucl = (URLClassLoader) HackyMinecraftEnv.class.getClassLoader();
        if (!removeBukkit) classLoader = new LaunchClassLoader(ucl.getURLs());
        else {
            List<URL> urls = new ArrayList<>();
            for (URL url : ucl.getURLs()) {
                if (url.toString().contains("bukkit"))
                    continue;
                urls.add(url);
            }
            classLoader = new LaunchClassLoader(urls.toArray(new URL[0]));
        }
        classloaderCallable = new ClassloaderHacks(classLoader);
    }

    public void initMinecraft() {
        classloaderCallable.inject(new Function<Void, Void>() {
            @Override
            public Void apply(Void unused) {
                try {
                    Loader.injectData("a", "b", "c", "d", "1.7.10", "e", new File(""), new ArrayList<String>());
                    Field side = FMLRelaunchLog.class.getDeclaredField("side");
                    side.setAccessible(true);
                    side.set(null, Side.SERVER);
                    side = FMLLaunchHandler.class.getDeclaredField("side");
                    side.setAccessible(true);
                    side.set(null, Side.SERVER);
                    Bootstrap.func_151354_b();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }).apply(null);
    }
    void call(Function<Void, Void> fun) {
        classloaderCallable.inject(fun).apply(null);
    }
}
