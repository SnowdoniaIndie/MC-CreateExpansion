package com.snowdonia.create_expansion.screen;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.screen.custom.BedrockExtractorMenu;
import com.snowdonia.create_expansion.screen.custom.WaterStrainerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Mod_Menus {

    public static final DeferredRegister<MenuType<?>> REGISTER_MENUS =
            DeferredRegister.create(Registries.MENU, CreateExpansion.MOD_ID);

    public static void register(IEventBus eventBus) {
        REGISTER_MENUS.register(eventBus);
    }

    public static final Supplier<MenuType<WaterStrainerMenu>> WATER_STRAINER_MENU =
            REGISTER_MENUS.register("water_strainer",
                    () -> IMenuTypeExtension.create(WaterStrainerMenu::new));

    public static final Supplier<MenuType<BedrockExtractorMenu>> BEDROCK_EXTRACTOR_MENU =
            REGISTER_MENUS.register("bedrock_extractor",
                    () -> IMenuTypeExtension.create(BedrockExtractorMenu::new));
}
