package com.snowdonia.create_expansion;

import com.snowdonia.create_expansion.item.Mod_Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Mod_CreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateExpansion.MOD_ID);

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static final Supplier<CreativeModeTab> BLACK_OPAL_ITEMS_TAB = CREATIVE_MODE_TABS.register("black_opal_items_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_expansion"))
            .icon(() -> new ItemStack(Mod_Items.ITEM_ADVANCED_MECHANISM.get()))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(Mod_Items.ITEM_ADVANCED_MECHANISM);
                output.accept(Mod_Items.ITEM_WATER_STRAINER);
                output.accept(Mod_Items.ITEM_BEDROCK_EXTRACTOR);
            })
            .build());
}
