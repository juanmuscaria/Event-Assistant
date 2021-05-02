import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class TestFakeEnviroment {

    @Test
    void initMinecraft() {
        HackyMinecraftEnv environment = new HackyMinecraftEnv();
        environment.initMinecraft();
        environment.call(new Function<Void, Void>() {
            @Override
            public Void apply(Void unused) {
                Bukkit.class.getName();
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Items.diamond).toString(), "minecraft:diamond");
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Blocks.stone).toString(), "minecraft:stone");
                return null;
            }
        });
    }

    @Test
    void assertNoBukkit() {
        HackyMinecraftEnv environment = new HackyMinecraftEnv(true);
        environment.initMinecraft();
        environment.call(new Function<Void, Void>() {
            @Override
            public Void apply(Void unused) {
                Error error = null;
                try {
                    Bukkit.class.getName();
                } catch (NoClassDefFoundError e) {
                    error = e;
                }
                Assertions.assertNotNull(error);
                return null;
            }
        });
    }

}
