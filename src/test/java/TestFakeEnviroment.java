import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFakeEnviroment {

    @Test
    public void initMinecraft() {
        ImSorryNotSorry environment = new ImSorryNotSorry();
        environment.initMinecraft();
        environment.callInsideLauncherClassLoader(new ImSorryNotSorry.Callable(){
            @Override
            void call() {
                Assertions.assertEquals(GameRegistry.findUniqueIdentifierFor(Items.diamond).toString(), "minecraft:diamond");
            }
        }.getClass());
    }
}
