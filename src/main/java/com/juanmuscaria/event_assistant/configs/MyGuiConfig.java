package com.juanmuscaria.event_assistant.configs;

import com.juanmuscaria.event_assistant.EventAssistant;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public class MyGuiConfig extends GuiConfig {

    public MyGuiConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(parentScreen), EventAssistant.MOD_ID, false, false, "EventAssistant-Configs");
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parent) {
        List<IConfigElement> list = new ArrayList<>();

        return list;
    }
}
