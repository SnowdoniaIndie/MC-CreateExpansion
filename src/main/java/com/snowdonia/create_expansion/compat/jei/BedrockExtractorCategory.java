package com.snowdonia.create_expansion.compat.jei;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.Mod_Blocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BedrockExtractorCategory extends AbstractRecipeCategory<BedrockExtractorJeiRecipe> {

    public static final RecipeType<BedrockExtractorJeiRecipe> RECIPE_TYPE =
            RecipeType.create(CreateExpansion.MOD_ID, "bedrock_extractor", BedrockExtractorJeiRecipe.class);

    public BedrockExtractorCategory(IGuiHelper guiHelper) {
        super(
                RECIPE_TYPE,
                Component.translatable("create_expansion.jei.category.bedrock_extractor"),
                guiHelper.createDrawableItemLike(Mod_Blocks.BEDROCK_EXTRACTOR.get()),
                120,
                18
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BedrockExtractorJeiRecipe recipe, IFocusGroup focuses) {
        builder.addOutputSlot(1, 1)
                .setStandardSlotBackground()
                .addItemStack(recipe.output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BedrockExtractorJeiRecipe recipe, IFocusGroup focuses) {
        int chancePercent = Math.round(recipe.percentage());
        // e.g. "Chance: 80%" (matches the vanilla composter category)
        Component text = Component.translatable(
                "create_expansion.jei.bedrock_extractor.chance", chancePercent);
        builder.addText(text, getWidth() - 24, getHeight())
                .setPosition(24, 0)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setColor(0xFF808080);
    }

    @Override
    public ResourceLocation getRegistryName(BedrockExtractorJeiRecipe recipe) {
        return recipe.uid();
    }
}
