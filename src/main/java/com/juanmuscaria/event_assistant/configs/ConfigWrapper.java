package com.juanmuscaria.event_assistant.configs;


import com.juanmuscaria.event_assistant.EventAssistant;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.Getter;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ConfigWrapper {

  @Getter
    private final Configuration config;

    public ConfigWrapper(File file) {
        config = new Configuration(file);
        FMLCommonHandler.instance().bus().register(this);
        config.load();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(EventAssistant.MOD_ID)) {
            loadDefaults();
        }
    }

    public void loadDefaults() {
        if(config.hasChanged()) {
            config.save();
        }
    }

    public enum Category {
      GENERAL("general", "General");
      @Getter
      private final String name;
      @Getter
      private final String displayName;

      Category(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
      }
    }
}
