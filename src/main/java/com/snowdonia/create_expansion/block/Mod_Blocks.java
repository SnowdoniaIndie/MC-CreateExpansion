package com.snowdonia.create_expansion.block;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.custom.BedrockExtractorBlock;
import com.snowdonia.create_expansion.block.custom.WaterStrainerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Mod_Blocks {

    public static final DeferredRegister.Blocks REGISTER_BLOCKS = DeferredRegister.createBlocks(CreateExpansion.MOD_ID);

    public static void register(IEventBus eventBus) {
        REGISTER_BLOCKS.register(eventBus);
    }

    public static final DeferredBlock<WaterStrainerBlock> WATER_STRAINER = REGISTER_BLOCKS.registerBlock(
            "water_strainer",
            WaterStrainerBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<BedrockExtractorBlock> BEDROCK_EXTRACTOR = REGISTER_BLOCKS.registerBlock(
            "bedrock_extractor",
            BedrockExtractorBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    );
}
