package com.snowdonia.create_expansion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Proof-of-concept for the Blaze Burner visual replacement (roadmap step 4).
 *
 * <p>Create draws an animated Blaze Burner in its JEI heat recipes (Mixing, etc.) via
 * {@link AnimatedBlazeBurner#draw}. This mixin cancels that and renders a plain Stone
 * block in its place, reusing the same transform Create uses so it sits correctly.
 *
 * <p>Global and client-only — every Create heat recipe that shows the animated burner
 * will show Stone instead. This is intentionally a throwaway test of the mechanism.
 */
@Mixin(AnimatedBlazeBurner.class)
public class AnimatedBlazeBurnerMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void createExpansion$renderStoneInstead(GuiGraphics graphics, int xOffset, int yOffset, CallbackInfo ci) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(xOffset, yOffset, 200);
        ms.mulPose(Axis.XP.rotationDegrees(-15.5f));
        ms.mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(Blocks.STONE.defaultBlockState())
                .atLocal(0, 1.65, 0)
                .scale(23)
                .render(graphics);
        ms.popPose();
        ci.cancel();
    }
}
