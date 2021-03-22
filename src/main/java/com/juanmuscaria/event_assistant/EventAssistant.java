package com.juanmuscaria.event_assistant;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = EventAssistant.MOD_ID, version = EventAssistant.VERSION)
public class EventAssistant {
    public static final String MOD_ID = "@{modId}";
    public static final String VERSION = "@{version}";

    @Mod.Instance(MOD_ID)
    public static EventAssistant mod;

    //Checking if a class exist is slow, cache it!
    private final boolean bukkit = ReflectionAssistant.doesClassExist("org.bukkit.Bukkit");

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    /**
     * Check if bukkit is present in this platform
     * @return true if bukkit is present.
     */
    public boolean hasBukkit() {
        return bukkit;
    }
}
