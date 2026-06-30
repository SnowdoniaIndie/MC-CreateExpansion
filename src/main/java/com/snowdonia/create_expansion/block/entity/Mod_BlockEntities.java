package com.snowdonia.create_expansion.block.entity;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.Mod_Blocks;
import com.snowdonia.create_expansion.block.entity.custom.BedrockExtractorBlockEntity;
import com.snowdonia.create_expansion.block.entity.custom.WaterStrainerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Mod_BlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER_BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateExpansion.MOD_ID);

    public static void register(IEventBus eventBus) {
        REGISTER_BLOCK_ENTITIES.register(eventBus);
    }

    public static final Supplier<BlockEntityType<WaterStrainerBlockEntity>> WATER_STRAINER_BE =
            REGISTER_BLOCK_ENTITIES.register("water_strainer",
                    () -> BlockEntityType.Builder.of(
                            WaterStrainerBlockEntity::new,
                            Mod_Blocks.WATER_STRAINER.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<BedrockExtractorBlockEntity>> BEDROCK_EXTRACTOR_BE =
            REGISTER_BLOCK_ENTITIES.register("bedrock_extractor",
                    () -> BlockEntityType.Builder.of(
                            BedrockExtractorBlockEntity::new,
                            Mod_Blocks.BEDROCK_EXTRACTOR.get()
                    ).build(null));
}
