package com.snowdonia.create_expansion.block.entity.custom;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.config.Mod_Config;
import com.snowdonia.create_expansion.util.GeneratorEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * A kinetic <em>consumer</em>: the inverse of Create's Creative Motor. Instead of
 * generating rotation it draws force from the network (see the stress impact registered
 * in {@code Mod_StressValues}) and converts that rotation into Forge Energy.
 *
 * <p>Each tick it generates FE proportional to shaft speed into a small internal buffer,
 * then pushes what it can into adjacent energy receivers on every face except the shaft
 * face. {@link KineticBlockEntity#getSpeed()} returns 0 when overstressed, so a network
 * that can't supply enough force simply produces nothing.
 */
public class ReverseMotorBlockEntity extends KineticBlockEntity {

    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(
            Mod_Config.REVERSE_MOTOR_ENERGY_CAPACITY.get(),
            Mod_Config.REVERSE_MOTOR_MAX_OUTPUT.get());

    public ReverseMotorBlockEntity(BlockPos pos, BlockState state) {
        super(Mod_BlockEntities.REVERSE_MOTOR_BE.get(), pos, state);
    }

    /** The energy buffer, exposed to automation through the capability system. */
    public IEnergyStorage getEnergyStorage() {
        return energy;
    }

    @Override
    public void tick() {
        super.tick();

        // Energy generation and transfer are server-authoritative; the client only needs
        // the visual rotation that KineticBlockEntity already handles.
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = generateEnergy();
        changed |= exportEnergy();
        if (changed) {
            setChanged();
        }
    }

    // Convert the current shaft speed into FE and buffer it. Returns true if anything was added.
    private boolean generateEnergy() {
        float speed = Math.abs(getSpeed());
        if (speed == 0) {
            return false;
        }
        int rate = (int) (speed * Mod_Config.REVERSE_MOTOR_FE_PER_RPM.get());
        return energy.generate(rate) > 0;
    }

    // Push buffered energy into adjacent receivers on every face but the shaft face.
    private boolean exportEnergy() {
        if (energy.getEnergyStored() == 0) {
            return false;
        }
        Direction shaftSide = getBlockState().getValue(DirectionalKineticBlock.FACING);
        boolean sent = false;
        for (Direction dir : Direction.values()) {
            if (dir == shaftSide || energy.getEnergyStored() == 0) {
                continue;
            }
            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK, worldPosition.relative(dir), dir.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }
            int extractable = energy.extractEnergy(Integer.MAX_VALUE, true);
            int accepted = target.receiveEnergy(extractable, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                sent = true;
            }
        }
        return sent;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        energy.setEnergy(compound.getInt("Energy"));
    }
}
