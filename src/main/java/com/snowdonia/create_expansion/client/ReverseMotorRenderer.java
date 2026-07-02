package com.snowdonia.create_expansion.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.snowdonia.create_expansion.block.entity.custom.ReverseMotorBlockEntity;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the rotating shaft on top of the static casing model. The base
 * {@link KineticBlockEntityRenderer} animates the returned model from the block's speed,
 * so we simply hand it Create's half-shaft partial — exactly like the Creative Motor.
 */
public class ReverseMotorRenderer extends KineticBlockEntityRenderer<ReverseMotorBlockEntity> {

    public ReverseMotorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ReverseMotorBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }
}
