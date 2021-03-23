import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFakeEnviroment {

    @Test
    public void initMinecraft() {
        HackyMinecraftEnv environment = new HackyMinecraftEnv();
        environment.initMinecraft();
        environment.callInsideLauncherClassLoader(new HackyMinecraftEnv.Callable(){
            @Override
            void call() {
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Items.diamond).toString(), "minecraft:diamond");
            }
        }.getClass());
    }
}
