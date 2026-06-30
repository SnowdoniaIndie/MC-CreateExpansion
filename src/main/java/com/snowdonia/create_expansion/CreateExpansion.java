package com.snowdonia.create_expansion;

import com.snowdonia.create_expansion.block.Mod_Blocks;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.event.Mod_Capabilities;
import com.snowdonia.create_expansion.item.Mod_Items;
import com.snowdonia.create_expansion.screen.Mod_MenuScreens;
import com.snowdonia.create_expansion.screen.Mod_Menus;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(CreateExpansion.MOD_ID)
public class CreateExpansion
{
    public static final String MOD_ID = "create_expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateExpansion(IEventBus eventBus, ModContainer modContainer)
    {
        Mod_CreativeTabs.register(eventBus);
        Mod_Blocks.register(eventBus);
        Mod_BlockEntities.register(eventBus);
        Mod_Items.register(eventBus);
        Mod_Capabilities.register(eventBus);
        Mod_Menus.register(eventBus);

        // The screen is a client-only class, so only register it on the client.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Mod_MenuScreens.register(eventBus);
        }
    }
}
