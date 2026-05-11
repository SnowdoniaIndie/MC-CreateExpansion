package com.snowdonia.create_expansion.item;

import com.snowdonia.create_expansion.CreateExpansion;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Mod_Items
{
    public static DeferredRegister.Items REGISTER_ITEMS = DeferredRegister.createItems(CreateExpansion.MOD_ID);

    public static void register(IEventBus eventBus)
    {
        REGISTER_ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> ITEM_ADVANCED_MECHANISM = REGISTER_ITEMS.registerSimpleItem("advanced_mechanism");
}
