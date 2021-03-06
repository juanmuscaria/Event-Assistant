package com.juanmuscaria.event_assistant.utils;

import com.juanmuscaria.event_assistant.interfacing.SafeInterfacing;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A set of utilities to work with items.
 */
public class ItemUtils {

    /**
     * Encodes an item stack into a string in the following format: <itemRegistry:metadata:amount>(nbt)
     *
     * @param stack - a bukkit or forge item stack to encode.
     * @return The encoded item stack.
     */
    public static @NotNull String encodeItem(Object stack) {
        if (SafeInterfacing.$.isBukkitStack(stack)) {
            stack = SafeInterfacing.$.toForgeStack(stack);
        }
        if (stack instanceof ItemStack) {
            ItemStack forgeStack = (ItemStack) stack;
            String item = GameRegistry.findUniqueIdentifierFor(forgeStack.getItem()).toString();
            item = '<' + item + ':' + forgeStack.getItemDamage() + ':' + forgeStack.stackSize + '>';
            if (forgeStack.stackTagCompound == null) return item + "()";
            else {
                return item + '(' + forgeStack.stackTagCompound.toString() + ')';
            }
        }
        throw new IllegalArgumentException("Object passed is not an item stack! " + stack.getClass().getName());
    }

    /**
     * Parses a encoded item stack with the <itemRegistry:metadata:amount>(nbt) format back into an ItemStack.
     *
     * @param serializedItem - the string which contains the serialized item.
     * @return A forge item stack of the item.
     * @throws BadStackFormatException  if it has an invalid stack string.
     * @throws BadNbtException          if it has an invalid nbt.
     * @throws MissingRegistryException if the item it tried to does not exist.
     */
    @NotNull
    public static ItemStack parseItem(String serializedItem) throws BadStackFormatException, BadNbtException, MissingRegistryException {
        int fistStackToken;
        int secondStackToken;
        int firstNBTToken;
        int secondNBTToken;
        int metadata;
        int amount;
        String nbt;
        String itemIdentifier;
        String registry;

        //Search for the nbt token.
        firstNBTToken = serializedItem.indexOf('(');
        secondNBTToken = serializedItem.lastIndexOf(')');
        if (firstNBTToken == -1 && secondNBTToken == -1) {
            //No nbt found.
            nbt = "";
        } else if (firstNBTToken != -1 && secondNBTToken != -1) {
            //Extract the nbt string.
            nbt = serializedItem.substring(firstNBTToken + 1, secondNBTToken);
            serializedItem = serializedItem.substring(0, firstNBTToken);
        } else {
            //Unbalanced nbt token, either corrupted or badly formatted.
            throw new BadStackFormatException("Invalid nbt identifier", serializedItem);
        }

        //Search for the item stack token.
        fistStackToken = serializedItem.indexOf('<');
        secondStackToken = serializedItem.lastIndexOf('>');
        if (fistStackToken == -1 || secondStackToken == -1) {
            //Unbalanced stack token, either corrupted or badly formatted.
            throw new BadStackFormatException("Invalid stack identifier", serializedItem);
        }

        //Extract all necessary information to reconstruct the stack.
        itemIdentifier = serializedItem.substring(fistStackToken + 1, secondStackToken);
        String[] splitIdentifier = itemIdentifier.split(":");
        if (splitIdentifier.length != 4) {
            //Missing or additional information, consider it invalid.
            throw new BadStackFormatException("Invalid stack identifier", serializedItem);
        }

        //Reconstruct the item registry and the stack attributes.
        registry = splitIdentifier[0] + ':' + splitIdentifier[1];
        try {
            metadata = Integer.parseInt(splitIdentifier[2]);
            amount = Integer.parseInt(splitIdentifier[3]);
            if (amount <= 0 || amount > 64)
                throw new NumberFormatException("Invalid stack size: " + amount);
        } catch (NumberFormatException e) {
            throw new BadStackFormatException("Metadata or amount is not a number:", serializedItem, e);
        }

        //Search for the item in the item registry.
        Item item = (Item) Item.itemRegistry.getObject(registry);
        if (item == null) {
            throw new MissingRegistryException(registry);
        }

        //Create a new item stack with all the parsed information.
        ItemStack itemStack = new ItemStack(item, amount, metadata);
        if (!nbt.isEmpty()) {
            NBTBase nbtbase;
            try {
                if (!((nbtbase = JsonToNBT.func_150315_a(nbt)) instanceof NBTTagCompound))
                    throw new BadNbtException(nbt);
                itemStack.setTagCompound((NBTTagCompound) nbtbase);
            } catch (NBTException e) {
                throw new BadNbtException(nbt, e);
            }
        }

        return itemStack;
    }

    /**
     * Same as {@link #parseItem(String)} but will return null if an exception arises while decoding the item.
     *
     * @param serializedItem - the string which contains the serialized item
     * @return A forge item stack of the item.
     */
    @Nullable
    public static ItemStack parseItemOrNull(String serializedItem) {
        try {
            return parseItem(serializedItem);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * This exception is thrown when an invalid nbt is parsed
     */
    public static class BadNbtException extends Exception {
        /**
         * The nbt which caused the exception
         */
        @Getter
        private final String nbt;

        BadNbtException(String nbt) {
            super("Invalid nbt tag: " + nbt);
            this.nbt = nbt;
        }

        BadNbtException(String nbt, Throwable parent) {
            super("Invalid nbt tag: " + nbt, parent);
            this.nbt = nbt;
        }
    }

    /**
     * This exception is thrown when an item is missing in the registry
     */
    public static class MissingRegistryException extends Exception {
        /**
         * The item registry which caused the exception
         */
        @Getter
        private final String registryComponent;

        MissingRegistryException(String registryComponent) {
            super("No such item:" + registryComponent);
            this.registryComponent = registryComponent;
        }
    }

    /**
     * This exception is thrown when an invalid item stack is parsed
     */
    public static class BadStackFormatException extends Exception {
        /**
         * The item stack which caused the exception
         */
        @Getter
        private final String badStack;

        BadStackFormatException(String reason, String stack) {
            super(reason + stack);
            this.badStack = stack;
        }

        BadStackFormatException(String reason, String stack, Throwable cause) {
            super(reason + stack, cause);
            this.badStack = stack;
        }
    }
}
