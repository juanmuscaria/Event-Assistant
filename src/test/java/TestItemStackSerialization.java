import com.juanmuscaria.event_assistant.utils.ItemUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//Target implementation <itemIdentifier:metadata:amount>(nbtTag)
public class TestItemStackSerialization {
    static HackyMinecraftEnv environment;

    @BeforeAll
    static void setup() {
        environment = new HackyMinecraftEnv(true);
        environment.initMinecraft();
    }

    @Test
    public void serialize() {
        environment.callInsideLauncherClassLoader(new HackyMinecraftEnv.Callable() {
            @Override
            void call() {
                ItemStack[] stackArray = new ItemStack[]{
                        new ItemStack(Items.diamond, 3, 5),
                        withNbt(new ItemStack(Blocks.stone, 7, 1), "{}"),
                        withNbt(new ItemStack(Blocks.dirt, 2, 4), "{tag1:\"something\",integer:5}")
                };
                Assertions.assertEquals("<minecraft:diamond:5:3>()", ItemUtils.encodeItem(stackArray[0]));
                Assertions.assertEquals("<minecraft:stone:1:7>({})", ItemUtils.encodeItem(stackArray[1]));
                Assertions.assertEquals("<minecraft:dirt:4:2>({tag1:\"something\",integer:5,})", ItemUtils.encodeItem(stackArray[2]));
            }
        }.getClass());
    }

    @Test
    public void deserialize() {
        environment.callInsideLauncherClassLoader(new HackyMinecraftEnv.Callable() {
            @Override
            void call() {
                String[] stackArray = new String[]{
                        "<minecraft:dirt:0:5>()", // 5 dirt
                        "<minecraft:wool:5:10>()", // 10 lime wool
                        "<minecraft:stone:0:7>({})", // 7 stones with empty nbt tag
                        "<minecraft:stone:0:7>({someTag:\"some text\"})"// 7 stones with some nbt tag
                };
                ItemStack dirt = ItemUtils.parseItemOrNull(stackArray[0]);
                Assertions.assertNotNull(dirt);
                Assertions.assertEquals("minecraft:dirt", GameRegistry.findUniqueIdentifierFor(dirt.getItem()).toString());
                Assertions.assertEquals(0, dirt.getItemDamage());
                Assertions.assertEquals(5, dirt.stackSize);
                Assertions.assertNull(dirt.stackTagCompound);
                ItemStack wool = ItemUtils.parseItemOrNull(stackArray[1]);
                Assertions.assertNotNull(wool);
                Assertions.assertEquals("minecraft:wool", GameRegistry.findUniqueIdentifierFor(wool.getItem()).toString());
                Assertions.assertEquals(5, wool.getItemDamage());
                Assertions.assertEquals(10, wool.stackSize);
                Assertions.assertNull(wool.stackTagCompound);
                ItemStack stone = ItemUtils.parseItemOrNull(stackArray[2]);
                Assertions.assertNotNull(stone);
                Assertions.assertEquals("minecraft:stone", GameRegistry.findUniqueIdentifierFor(stone.getItem()).toString());
                Assertions.assertEquals(0, stone.getItemDamage());
                Assertions.assertEquals(7, stone.stackSize);
                Assertions.assertNotNull(stone.stackTagCompound);
                ItemStack stoneNbt = ItemUtils.parseItemOrNull(stackArray[3]);
                Assertions.assertNotNull(stoneNbt);
                Assertions.assertEquals("minecraft:stone", GameRegistry.findUniqueIdentifierFor(stoneNbt.getItem()).toString());
                Assertions.assertEquals(0, stoneNbt.getItemDamage());
                Assertions.assertEquals(7, stoneNbt.stackSize);
                Assertions.assertNotNull(stoneNbt.stackTagCompound);
                Assertions.assertEquals("some text", stoneNbt.stackTagCompound.getString("someTag"));
            }
        }.getClass());
    }

    //Non static calls in the injected class will give a NPE
    static ItemStack withNbt(ItemStack stack, String nbt) {
        try {
            stack.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(nbt));
        } catch (NBTException e) {
            throw new IllegalArgumentException(nbt);
        }
        return stack;
    }
}
