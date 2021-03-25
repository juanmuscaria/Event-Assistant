package com.juanmuscaria.event_assistant.interfacing;

import com.juanmuscaria.event_assistant.EventAssistant;
import net.minecraft.item.ItemStack;

public interface SafeInterfacing {
    /**
     * Get a safe way to interface between bukkit/forge without vomiting errors if bukkit is missing
     */
    SafeInterfacing $ = Provider.get();

    /**
     * Safe check to see if an item stack is from bukkit.
     * @param stack stack to check
     * @return will return true if the stack is an "org.bukkit.inventory.ItemStack"
     */
    boolean isBukkitStack(Object stack);

    /**
     * Converts a bukkit stack to forge stack
     * @param bukkitStack a "org.bukkit.inventory.ItemStack"
     * @return "net.minecraft.item.ItemStack"
     * @throws UnsupportedOperationException if bukkit is missing
     */
    ItemStack toForgeStack(Object bukkitStack);

    /**
     * Converts an forge stack into a bukkit stack
     * @param forgeStack A "net.minecraft.item.ItemStack"
     * @return "org.bukkit.inventory.ItemStack"
     * @throws UnsupportedOperationException if bukkit is missing
     */
    Object toBukkitStack(Object forgeStack);
}

class Provider {
    static SafeInterfacing get() {
        return EventAssistant.hasBukkit() ? new WithBukkit() : new WithoutBukkit();
    }
}
