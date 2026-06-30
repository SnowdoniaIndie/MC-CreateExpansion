package com.snowdonia.create_expansion.block.custom;

import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.block.entity.custom.BedrockExtractorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class BedrockExtractorBlock extends Block implements EntityBlock {

    public BedrockExtractorBlock(Properties properties) {
        super(properties);
    }

    // Open the inventory GUI on right-click (server decides; client just plays along).
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof BedrockExtractorBlockEntity extractor) {
                // Send the block position so the client menu can locate this block entity.
                serverPlayer.openMenu(extractor, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    // ----- Block entity wiring -----

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BedrockExtractorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Only tick on the server; rendering/logic stays authoritative there.
        if (level.isClientSide()) {
            return null;
        }
        return type == Mod_BlockEntities.BEDROCK_EXTRACTOR_BE.get()
                ? (lvl, pos, st, be) -> BedrockExtractorBlockEntity.serverTick(lvl, pos, st, (BedrockExtractorBlockEntity) be)
                : null;
    }

    // Drop the inventory contents when the block is broken/replaced.
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BedrockExtractorBlockEntity extractor) {
                IItemHandler inventory = extractor.getInventory();
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    popResource(level, pos, inventory.getStackInSlot(slot));
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
