package com.snowdonia.create_expansion.compat.jei;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.Mod_Blocks;
import com.snowdonia.create_expansion.screen.custom.BedrockExtractorScreen;
import com.snowdonia.create_expansion.screen.custom.WaterStrainerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CreateExpansionJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "jei_plugin");

    // The bubble progress area (x, y, w, h) matches the gauge drawn by both screens.
    private static final int BUBBLE_X = 82;
    private static final int BUBBLE_Y = 18;
    private static final int BUBBLE_W = 12;
    private static final int BUBBLE_H = 29;

    // Turns four parsed loot fields into a concrete JEI recipe row for a given category.
    @FunctionalInterface
    private interface RecipeFactory<T> {
        T create(ItemStack output, int weight, float percentage, ResourceLocation uid);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WaterStrainerCategory(guiHelper),
                new BedrockExtractorCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WaterStrainerCategory.RECIPE_TYPE,
                loadRecipes("water_strainer", WaterStrainerJeiRecipe::new));
        registration.addRecipes(BedrockExtractorCategory.RECIPE_TYPE,
                loadRecipes("bedrock_extractor", BedrockExtractorJeiRecipe::new));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Each block acts as the icon/catalyst for its own category.
        registration.addRecipeCatalyst(Mod_Blocks.WATER_STRAINER.get(), WaterStrainerCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(Mod_Blocks.BEDROCK_EXTRACTOR.get(), BedrockExtractorCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Hovering/clicking the bubble progress area shows the recipes, like the furnace's arrow.
        registration.addRecipeClickArea(WaterStrainerScreen.class,
                BUBBLE_X, BUBBLE_Y, BUBBLE_W, BUBBLE_H, WaterStrainerCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(BedrockExtractorScreen.class,
                BUBBLE_X, BUBBLE_Y, BUBBLE_W, BUBBLE_H, BedrockExtractorCategory.RECIPE_TYPE);
    }

    /**
     * Reads the bundled loot table for the given block and turns each weighted item entry
     * into a JEI recipe. Datapacks aren't available to the client, so we parse the file
     * straight from the mod jar.
     *
     * @param blockName the block id used both for the loot table path and the recipe uid prefix
     * @param factory   builds the category-specific recipe record
     */
    private static <T> List<T> loadRecipes(String blockName, RecipeFactory<T> factory) {
        String resource = "/data/" + CreateExpansion.MOD_ID + "/loot_table/gameplay/" + blockName + ".json";
        List<T> recipes = new ArrayList<>();
        try (InputStream in = CreateExpansionJeiPlugin.class.getResourceAsStream(resource)) {
            if (in == null) {
                CreateExpansion.LOGGER.warn("JEI: could not find loot table resource {}", resource);
                return recipes;
            }
            JsonObject root = JsonParser.parseReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray pools = root.getAsJsonArray("pools");
            if (pools == null) {
                return recipes;
            }

            for (JsonElement poolElement : pools) {
                JsonArray entries = poolElement.getAsJsonObject().getAsJsonArray("entries");
                if (entries == null) {
                    continue;
                }

                // First pass: total weight of the item entries in this pool, so we can show a percentage.
                int totalWeight = 0;
                for (JsonElement entryElement : entries) {
                    JsonObject entry = entryElement.getAsJsonObject();
                    if (isItemEntry(entry)) {
                        totalWeight += weightOf(entry);
                    }
                }
                if (totalWeight <= 0) {
                    continue;
                }

                // Second pass: one JEI recipe per item entry.
                for (JsonElement entryElement : entries) {
                    JsonObject entry = entryElement.getAsJsonObject();
                    if (!isItemEntry(entry)) {
                        continue;
                    }
                    int weight = weightOf(entry);
                    ResourceLocation itemId = ResourceLocation.parse(entry.get("name").getAsString());
                    ItemStack output = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
                    float percentage = weight * 100f / totalWeight;
                    ResourceLocation uid = ResourceLocation.fromNamespaceAndPath(
                            CreateExpansion.MOD_ID,
                            blockName + "/" + itemId.getNamespace() + "/" + itemId.getPath());
                    recipes.add(factory.create(output, weight, percentage, uid));
                }
            }
        } catch (Exception e) {
            CreateExpansion.LOGGER.error("JEI: failed to read loot table {}", resource, e);
        }
        return recipes;
    }

    private static boolean isItemEntry(JsonObject entry) {
        return entry.has("type")
                && "minecraft:item".equals(entry.get("type").getAsString())
                && entry.has("name");
    }

    private static int weightOf(JsonObject entry) {
        return entry.has("weight") ? entry.get("weight").getAsInt() : 1;
    }
}
