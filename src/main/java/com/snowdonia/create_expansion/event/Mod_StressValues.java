package com.snowdonia.create_expansion.event;

import com.simibubi.create.api.stress.BlockStressValues;
import com.snowdonia.create_expansion.block.Mod_Blocks;
import com.snowdonia.create_expansion.config.Mod_Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Registers stress impacts for the mod's kinetic blocks. Create reads these suppliers
 * lazily whenever it needs a block's impact, so the value tracks the live config.
 */
public class Mod_StressValues {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Mod_StressValues::registerStressValues);
    }

    private static void registerStressValues(FMLCommonSetupEvent event) {
        // The registry isn't guaranteed thread-safe, so register on the main thread.
        event.enqueueWork(() -> BlockStressValues.IMPACTS.register(
                Mod_Blocks.REVERSE_MOTOR.get(),
                () -> Mod_Config.REVERSE_MOTOR_STRESS_IMPACT.get()));
    }
}
