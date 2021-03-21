package com.juanmuscaria.event_assistant;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = EventAssistant.MODID, version = EventAssistant.VERSION)
public class EventAssistant {
    public static final String MODID = "@{modId}";
    public static final String VERSION = "@{version}";

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
