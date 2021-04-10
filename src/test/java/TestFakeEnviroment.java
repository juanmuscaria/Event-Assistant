import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFakeEnviroment {

    @Test
    public void initMinecraft() {
        HackyMinecraftEnv environment = new HackyMinecraftEnv();
        environment.initMinecraft();
        environment.callInsideLauncherClassLoader(new HackyMinecraftEnv.Callable() {
            @Override
            void call() {
                Bukkit.class.getName();
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Items.diamond).toString(), "minecraft:diamond");
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Blocks.stone).toString(), "minecraft:stone");
            }
        }.getClass());
    }

    @Test
    public void assertNoBukkit() {
        HackyMinecraftEnv environment = new HackyMinecraftEnv(true);
        environment.initMinecraft();
        environment.callInsideLauncherClassLoader(new HackyMinecraftEnv.Callable() {
            @Override
            void call() {
                Error error = null;
                try {
                    Bukkit.class.getName();
                } catch (NoClassDefFoundError e) {
                    error = e;
                }
                Assertions.assertNotNull(error);
            }
        }.getClass());
    }

}
