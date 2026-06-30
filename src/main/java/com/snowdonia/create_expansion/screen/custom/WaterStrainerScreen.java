package com.snowdonia.create_expansion.screen.custom;

import com.snowdonia.create_expansion.CreateExpansion;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WaterStrainerScreen extends AbstractContainerScreen<WaterStrainerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "textures/gui/water_strainer.png");

    // Referenced from vanilla at runtime (not bundled): the brewing-stand "bubbles" sprite for
    // the rising foreground, and the unfilled bubble region from the main brewing-stand GUI texture.
    private static final ResourceLocation BUBBLES_SPRITE =
            ResourceLocation.withDefaultNamespace("container/brewing_stand/bubbles");
    private static final ResourceLocation BREWING_STAND_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    // Position of the bubble tube within the vanilla brewing-stand GUI texture (256x256).
    private static final int BREWING_BUBBLES_U = 63;
    private static final int BREWING_BUBBLES_V = 14;
    private static final int BUBBLES_WIDTH = 12;
    private static final int BUBBLES_HEIGHT = 29;
    private static final int BUBBLES_X = 82;    // centered: (176 - 12) / 2
    private static final int BUBBLES_TOP = 18;  // sits just above the slot row at y=54

    public WaterStrainerScreen(WaterStrainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Unfilled bubble track: the region behind the bubbles in the vanilla brewing-stand GUI
        // (brewing_stand.png is 256x256, so the 256-assuming blit overload is correct here).
        guiGraphics.blit(BREWING_STAND_TEXTURE,
                x + BUBBLES_X, y + BUBBLES_TOP,
                BREWING_BUBBLES_U, BREWING_BUBBLES_V,
                BUBBLES_WIDTH, BUBBLES_HEIGHT);

        // Bubbles rise from the bottom as progress fills (vanilla brewing-stand sprite).
        int bubbleHeight = this.menu.getScaledProgress(BUBBLES_HEIGHT);
        if (bubbleHeight > 0) {
            guiGraphics.blitSprite(
                    BUBBLES_SPRITE,
                    BUBBLES_WIDTH, BUBBLES_HEIGHT,
                    0, BUBBLES_HEIGHT - bubbleHeight,
                    x + BUBBLES_X, y + BUBBLES_TOP + BUBBLES_HEIGHT - bubbleHeight,
                    BUBBLES_WIDTH, bubbleHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // Draws the hovered-item tooltip on top of everything.
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
