package com.snowdonia.create_expansion.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.Mod_Blocks;
import net.minecraft.world.item.BlockItem;
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
    public static final DeferredItem<Item> ITEM_ADVANCED_MECHANISM_SEQUENCED = REGISTER_ITEMS.registerItem("incomplete_advanced_mechanism", SequencedAssemblyItem::new, new Item.Properties());

    // The placeable item form of the Water Strainer block.
    public static final DeferredItem<BlockItem> ITEM_WATER_STRAINER = REGISTER_ITEMS.registerSimpleBlockItem(Mod_Blocks.WATER_STRAINER);

    // The placeable item form of the Bedrock Extractor block.
    public static final DeferredItem<BlockItem> ITEM_BEDROCK_EXTRACTOR = REGISTER_ITEMS.registerSimpleBlockItem(Mod_Blocks.BEDROCK_EXTRACTOR);
}
