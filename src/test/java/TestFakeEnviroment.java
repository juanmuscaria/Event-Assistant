import net.minecraft.init.Items;
import org.junit.jupiter.api.Test;

public class TestFakeEnviroment {

    @Test
    public void initMinecraft() {
        ImSorryNotSorry environment = new ImSorryNotSorry();
        environment.initMinecraft();
        environment.callInsideLauncherClassLoader(new ImSorryNotSorry.Callable(){
            @Override
            void call() {
                System.out.println(Items.diamond.getClass());
            }
        }.getClass());
    }
}
