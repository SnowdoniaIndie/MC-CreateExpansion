package com.snowdonia.create_expansion.util;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * A Forge Energy buffer for a generator block: external machines and cables may pull energy
 * out (up to {@code maxExtract} per operation) but can never push energy in. The owning
 * block entity feeds it internally via {@link #generate(int)}.
 */
public class GeneratorEnergyStorage extends EnergyStorage {

    public GeneratorEnergyStorage(int capacity, int maxExtract) {
        // maxReceive = 0: nothing outside can insert energy.
        super(capacity, 0, maxExtract);
    }

    /**
     * Adds internally-generated energy, bypassing the (zero) receive limit.
     *
     * @return the amount actually stored (0 if the buffer was already full)
     */
    public int generate(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int added = Math.min(capacity - energy, amount);
        energy += added;
        return added;
    }

    /** Overwrites the stored amount (used when loading from NBT), clamped to the capacity. */
    public void setEnergy(int stored) {
        this.energy = Math.max(0, Math.min(capacity, stored));
    }
}
