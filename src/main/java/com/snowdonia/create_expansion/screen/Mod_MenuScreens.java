package com.snowdonia.create_expansion.screen;

import com.snowdonia.create_expansion.screen.custom.BedrockExtractorScreen;
import com.snowdonia.create_expansion.screen.custom.WaterStrainerScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

// Client-only: links the menu type to the screen that renders it.
// Registered from the mod constructor only when running on the client.
public class Mod_MenuScreens {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Mod_MenuScreens::onRegisterScreens);
    }

    private static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(Mod_Menus.WATER_STRAINER_MENU.get(), WaterStrainerScreen::new);
        event.register(Mod_Menus.BEDROCK_EXTRACTOR_MENU.get(), BedrockExtractorScreen::new);
    }
}
