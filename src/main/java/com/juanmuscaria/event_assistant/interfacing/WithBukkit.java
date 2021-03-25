package com.juanmuscaria.event_assistant.interfacing;

import com.juanmuscaria.event_assistant.ReflectionAssistant;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

final class WithBukkit implements SafeInterfacing {
    private static final ReflectionAssistant.MethodInvoker CraftItemStack_asNMSCopy =
            ReflectionAssistant.getMethod(CraftItemStack.class, "asNMSCopy", org.bukkit.inventory.ItemStack.class);
    private static final ReflectionAssistant.MethodInvoker CraftItemStack_asBukkitCopy =
            ReflectionAssistant.getMethod(CraftItemStack.class, "asBukkitCopy", net.minecraft.item.ItemStack.class);

    @Override
    public boolean isBukkitStack(Object stack) {
        return stack instanceof org.bukkit.inventory.ItemStack;
    }

    @Override
    public ItemStack toForgeStack(Object bukkitStack) {
        return (ItemStack) CraftItemStack_asNMSCopy.invoke(null, bukkitStack);
    }

    @Override
    public Object toBukkitStack(Object forgeStack) {
        return CraftItemStack_asBukkitCopy.invoke(null, forgeStack);
    }

}
