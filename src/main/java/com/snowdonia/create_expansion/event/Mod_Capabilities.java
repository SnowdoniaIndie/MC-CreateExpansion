package com.snowdonia.create_expansion.event;

import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class Mod_Capabilities {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Mod_Capabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Both blocks behave the same: extract-only, top and bottom faces only.
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Mod_BlockEntities.WATER_STRAINER_BE.get(),
                // Only the top and bottom faces expose an inventory, and that view is
                // extract-only — automation can pull items out but never pipe them in.
                // The four horizontal sides (and the null "any" side) expose nothing.
                (blockEntity, side) -> (side == Direction.UP || side == Direction.DOWN)
                        ? blockEntity.getExtractOnlyInventory()
                        : null
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Mod_BlockEntities.BEDROCK_EXTRACTOR_BE.get(),
                (blockEntity, side) -> (side == Direction.UP || side == Direction.DOWN)
                        ? blockEntity.getExtractOnlyInventory()
                        : null
        );
    }
}
