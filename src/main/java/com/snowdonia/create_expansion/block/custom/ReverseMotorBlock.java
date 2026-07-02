package com.snowdonia.create_expansion.block.custom;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.block.entity.custom.ReverseMotorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Reverse Motor: a clone of Create's Creative Motor that consumes rotation instead of
 * producing it. It behaves as an ordinary kinetic component with a shaft on its
 * {@link #FACING} face, and generates Forge Energy while that shaft is turning (see
 * {@link com.snowdonia.create_expansion.block.entity.custom.ReverseMotorBlockEntity}).
 */
public class ReverseMotorBlock extends DirectionalKineticBlock implements IBE<ReverseMotorBlockEntity> {

    public ReverseMotorBlock(Properties properties) {
        super(properties);
    }

    // Reuse the motor's collision shape so the casing + shaft line up with the model.
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    // ----- IRotate: the shaft sticks out of the FACING side -----

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    // ----- Block entity wiring (IBE provides newBlockEntity + ticker) -----

    @Override
    public Class<ReverseMotorBlockEntity> getBlockEntityClass() {
        return ReverseMotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReverseMotorBlockEntity> getBlockEntityType() {
        return Mod_BlockEntities.REVERSE_MOTOR_BE.get();
    }
}
