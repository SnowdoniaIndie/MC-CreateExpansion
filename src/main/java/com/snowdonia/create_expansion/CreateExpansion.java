package com.snowdonia.create_expansion;

import com.snowdonia.create_expansion.item.Mod_Items;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(CreateExpansion.MOD_ID)
public class CreateExpansion
{
    public static final String MOD_ID = "create_expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateExpansion(IEventBus eventBus, ModContainer modContainer)
    {
        Mod_CreativeTabs.register(eventBus);
        Mod_Items.register(eventBus);
    }
}
