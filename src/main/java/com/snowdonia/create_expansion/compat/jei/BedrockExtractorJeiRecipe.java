package com.snowdonia.create_expansion.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * One row in the Bedrock Extractor JEI category: a produced item, its raw loot weight,
 * and the resulting chance as a percentage of the pool.
 */
public record BedrockExtractorJeiRecipe(ItemStack output, int weight, float percentage, ResourceLocation uid) {
}
