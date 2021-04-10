package com.juanmuscaria.event_assistant;

import com.juanmuscaria.event_assistant.configs.ConfigWrapper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;

/**
 * Main mod class, it will mostly contain internal usage stuff but some things may be useful.
 */
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
    private ConfigWrapper configs;
    @Getter
    private File dataFolder;

    @SneakyThrows
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        dataFolder = new File(event.getModConfigurationDirectory(), MOD_ID);
        Files.createDirectories(dataFolder.toPath());
        configs = new ConfigWrapper(new File(dataFolder, "config.cfg"));
    }

    /**
     * Check if bukkit is present in this platform
     * @return true if bukkit is present.
     */
    public static boolean hasBukkit() {
        return bukkit;
    }

}
