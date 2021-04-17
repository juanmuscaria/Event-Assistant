package com.juanmuscaria.event_assistant;

import com.juanmuscaria.event_assistant.configs.ConfigWrapper;
import com.juanmuscaria.event_assistant.utils.TaskHandler;
import com.juanmuscaria.event_assistant.utils.tracking.TileTracker;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;

/**
 * Main mod class, it will mostly contain internal usage stuff but some things may be useful.
 *
 * @author juanmuscaria
 */
@Mod(modid = EventAssistant.MOD_ID,
        guiFactory = "com.juanmuscaria.event_assistant.configs.ConfigGuiFactory")
public class EventAssistant {
    public static final String MOD_ID = "EventAssistant";
    /**
     * Mod instance singleton.
     */
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
        if (!TileTracker.isAvailable()) {
            logger.warn(" ");
            logger.warn("Tile tracking is not available!");
            logger.warn("Please install Grimoire to allow tiles to be tracked.");
            logger.warn("https://github.com/CrucibleMC/Grimoire");
            logger.warn(" ");
        }

        FMLCommonHandler.instance().bus().register(TaskHandler.INSTANCE);
        if (TileTracker.isAvailable()) {
            MinecraftForge.EVENT_BUS.register(TileTrackingListener.INSTANCE);
        }
    }

    /**
     * Check if bukkit is present in this platform.
     *
     * @return True if bukkit is present.
     */
    public static boolean hasBukkit() {
        return bukkit;
    }

}
