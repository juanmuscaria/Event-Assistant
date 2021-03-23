package com.juanmuscaria.event_assistant;

import com.juanmuscaria.event_assistant.configs.ConfigWrapper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

@Mod(modid = EventAssistant.MOD_ID,
        version = EventAssistant.VERSION,
        guiFactory = "com.juanmuscaria.event_assistant.configs.ConfigGuiFactory")
public class EventAssistant {
    public static final String MOD_ID = "@{modId}";
    public static final String VERSION = "@{version}";
    @Mod.Instance(MOD_ID)
    public static EventAssistant mod;
    //Checking if a class exist is slow, cache it!
    private static final boolean bukkit = ReflectionAssistant.doesClassExist("org.bukkit.Bukkit");

    @Getter
    private Logger logger;
    @Getter
    private ConfigWrapper configWrapper;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        configWrapper = new ConfigWrapper(event.getSuggestedConfigurationFile());
    }

    /**
     * Check if bukkit is present in this platform
     * @return true if bukkit is present.
     */
    public static boolean hasBukkit() {
        return bukkit;
    }

}
