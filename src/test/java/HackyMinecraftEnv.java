import com.google.common.io.ByteStreams;
import com.juanmuscaria.event_assistant.ReflectionAssistant;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Bootstrap;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HackyMinecraftEnv {
    final URLClassLoader ucl;
    final LaunchClassLoader classLoader;
    final ReflectionAssistant.MethodInvoker defineClass = ReflectionAssistant.getMethod(ClassLoader.class,"defineClass", String.class, byte[].class, int.class, int.class);
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
    }

    public void initMinecraft() {
        callInsideLauncherClassLoader(new Callable(){
            @Override
            void call() {
                try {
                    Loader.injectData(new Object[]{"a", "b", "c", "d", "1.7.10", "e", new File(""), new ArrayList<String>()});
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
            }
        }.getClass());
    }

    public void callInsideLauncherClassLoader(Class<? extends Callable> callable) {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> clazz = injectClass(callable);
        try {
            Method callMe = clazz.getDeclaredMethod("call");
            callMe.setAccessible(true);
            Constructor<?> c = clazz.getDeclaredConstructors()[0];
            c.setAccessible(true);
            callMe.invoke(c.newInstance((Object) null));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    public Class<?> injectClass(Class<?> clazz)
    {
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class"))
        {
            byte[] bytes = ByteStreams.toByteArray(in);
            return (Class<?>) defineClass.invoke(classLoader, clazz.getName(), bytes, 0, bytes.length);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            return null;
        }
    }

    public static abstract class Callable {
        public Callable(){ }

        abstract void call();
    }
}
