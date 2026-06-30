package com.snowdonia.create_expansion.screen.custom;

import com.snowdonia.create_expansion.CreateExpansion;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BedrockExtractorScreen extends AbstractContainerScreen<BedrockExtractorMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "textures/gui/bedrock_extractor.png");

    // Our own foreground bubble texture (12x29) so it can be repainted / resource-pack
    // overridden. The unfilled track is baked into the main panel texture (like vanilla bakes
    // it into brewing_stand.png), so only the rising foreground is drawn here at runtime.
    private static final ResourceLocation BUBBLES_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "textures/gui/bedrock_extractor_bubbles.png");
    private static final int BUBBLES_WIDTH = 12;
    private static final int BUBBLES_HEIGHT = 29;
    private static final int BUBBLES_X = 82;    // centered: (176 - 12) / 2
    private static final int BUBBLES_TOP = 18;  // sits just above the slot row at y=54

    public BedrockExtractorScreen(BedrockExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // The unfilled track is part of the panel texture; just draw the rising foreground.
        // Bubbles rise from the bottom as progress fills.
        int bubbleHeight = this.menu.getScaledProgress(BUBBLES_HEIGHT);
        if (bubbleHeight > 0) {
            guiGraphics.blit(BUBBLES_TEXTURE,
                    x + BUBBLES_X, y + BUBBLES_TOP + BUBBLES_HEIGHT - bubbleHeight,
                    0, 0.0F, (float) (BUBBLES_HEIGHT - bubbleHeight),
                    BUBBLES_WIDTH, bubbleHeight, BUBBLES_WIDTH, BUBBLES_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // Draws the hovered-item tooltip on top of everything.
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
