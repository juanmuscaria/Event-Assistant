package com.juanmuscaria.event_assistant.interfacing;

import net.minecraft.item.ItemStack;

final class WithoutBukkit implements SafeInterfacing {
    @Override
    public boolean isBukkitStack(Object stack) {
        return false;
    }

    @Override
    public ItemStack toForgeStack(Object bukkitStack) {
        throw new UnsupportedOperationException("Bukkit is needed for this operation");
    }

    @Override
    public Object toBukkitStack(Object forgeStack) {
        throw new UnsupportedOperationException("Bukkit is needed for this operation");
    }
}
