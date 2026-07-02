package com.snowdonia.create_expansion.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-only registration of the Reverse Motor's rendering. Called from the mod
 * constructor's client branch so none of these classes are touched on a dedicated server.
 *
 * <p>Create 6 renders kinetic parts through Flywheel when its backend is active, falling
 * back to a vanilla {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer}
 * otherwise — so we register both, exactly like the Creative Motor.
 */
public class Mod_BlockEntityRenderers {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Mod_BlockEntityRenderers::onRegisterRenderers);
        eventBus.addListener(Mod_BlockEntityRenderers::onClientSetup);
    }

    // Fallback path: used when Flywheel visualization is unavailable.
    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Mod_BlockEntities.REVERSE_MOTOR_BE.get(), ReverseMotorRenderer::new);
    }

    // Primary path: the rotating shaft as a Flywheel visual. Spins the SHAFT_HALF partial
    // toward the block's facing, and skips the vanilla renderer while the visual is active.
    private static void onClientSetup(FMLClientSetupEvent event) {
        SimpleBlockEntityVisualizer.builder(Mod_BlockEntities.REVERSE_MOTOR_BE.get())
                .factory(OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
                .skipVanillaRender(be -> true)
                .apply();
    }
}
