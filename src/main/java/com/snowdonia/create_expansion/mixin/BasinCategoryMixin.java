package com.snowdonia.create_expansion.mixin;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Proof-of-concept companion to {@link AnimatedBlazeBurnerMixin} (roadmap step 4).
 *
 * <p>Create's basin/mixing JEI category adds a small render-only item icon for the heat
 * requirement via {@code AllBlocks.BLAZE_BURNER.asStack()} in {@code setRecipe}. That call
 * is a unique {@code BlockEntry.asStack()} in the method (recipe outputs don't use it), so
 * redirecting it swaps just the Blaze Burner icon to Stone without touching outputs.
 *
 * <p>The separate Blaze Cake catalyst (superheated recipes only) is left alone — it's a
 * fuel item, not the burner.
 */
@Mixin(BasinCategory.class)
public class BasinCategoryMixin {

    @Redirect(
            method = "setRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack createExpansion$replaceBurnerIcon(BlockEntry<?> instance) {
        return new ItemStack(Items.STONE);
    }
}
