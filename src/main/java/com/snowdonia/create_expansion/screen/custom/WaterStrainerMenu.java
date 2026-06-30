package com.snowdonia.create_expansion.screen.custom;

import com.snowdonia.create_expansion.block.Mod_Blocks;
import com.snowdonia.create_expansion.block.entity.custom.WaterStrainerBlockEntity;
import com.snowdonia.create_expansion.screen.Mod_Menus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class WaterStrainerMenu extends AbstractContainerMenu {

    private static final int STRAINER_SLOT_COUNT = WaterStrainerBlockEntity.INVENTORY_SIZE; // 9

    private final WaterStrainerBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    // Client-side constructor: the block position is read back out of the network buffer.
    // A blank ContainerData is created here; the server syncs the real values into it.
    public WaterStrainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData), new SimpleContainerData(2));
    }

    // Server-side constructor.
    public WaterStrainerMenu(int containerId, Inventory playerInventory, WaterStrainerBlockEntity blockEntity, ContainerData data) {
        super(Mod_Menus.WATER_STRAINER_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        // The strainer's own inventory: a single row of 9 slots, aligned with a chest's
        // bottom storage row (y=52).
        IItemHandler handler = blockEntity.getInventory();
        for (int slot = 0; slot < STRAINER_SLOT_COUNT; slot++) {
            this.addSlot(new SlotItemHandler(handler, slot, 8 + slot * 18, 52));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // Registers the progress fields so they sync to the client each tick.
        addDataSlots(data);
    }

    /**
     * Progress scaled to a pixel width, for drawing the progress bar.
     * @param pixels the full width of the bar when complete
     */
    public int getScaledProgress(int pixels) {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        if (maxProgress == 0) {
            return 0;
        }
        return progress * pixels / maxProgress;
    }

    private static WaterStrainerBlockEntity getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        if (blockEntity instanceof WaterStrainerBlockEntity strainer) {
            return strainer;
        }
        throw new IllegalStateException("No Water Strainer block entity at " + pos);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Handles shift-clicking a stack between the strainer and the player inventory.
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMoved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack rawStack = slot.getItem();
            quickMoved = rawStack.copy();
            if (index < STRAINER_SLOT_COUNT) {
                // Moving out of the strainer into the player's inventory.
                if (!this.moveItemStackTo(rawStack, STRAINER_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from the player's inventory into the strainer.
                if (!this.moveItemStackTo(rawStack, 0, STRAINER_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (rawStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return quickMoved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Mod_Blocks.WATER_STRAINER.get());
    }
}
