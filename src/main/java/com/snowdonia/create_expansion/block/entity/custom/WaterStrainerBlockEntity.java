package com.snowdonia.create_expansion.block.entity.custom;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.config.Mod_Config;
import com.snowdonia.create_expansion.screen.custom.WaterStrainerMenu;
import com.snowdonia.create_expansion.util.ExtractOnlyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class WaterStrainerBlockEntity extends BlockEntity implements MenuProvider {

    /** Number of inventory slots. */
    public static final int INVENTORY_SIZE = 9;
    /** How many horizontally-adjacent blocks must be water for the strainer to work. */
    private static final int REQUIRED_WATER_NEIGHBOURS = 2;
    /** Loot table rolled to decide what the strainer produces. Lives at
     *  data/create_expansion/loot_table/gameplay/water_strainer.json */
    private static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "gameplay/water_strainer"));

    // The 9-slot inventory. setChanged() marks the BE dirty so it gets saved.
    private final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // The view exposed to hoppers/pipes: extract allowed, insert never.
    private final ExtractOnlyItemHandler extractOnlyInventory = new ExtractOnlyItemHandler(inventory);

    // Counts up each tick while in water; resets after producing an item.
    private int progress = 0;

    // Syncs progress to the open menu/screen so the progress bar updates live.
    // index 0 = current progress, index 1 = the interval it's counting toward.
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> processingTicks();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                progress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public WaterStrainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Mod_BlockEntities.WATER_STRAINER_BE.get(), pos, blockState);
    }

    /** The real inventory, used internally by the GUI, generation, and drops. */
    public ItemStackHandler getInventory() {
        return inventory;
    }

    /** The extract-only view handed to automation through the capability system. */
    public IItemHandler getExtractOnlyInventory() {
        return extractOnlyInventory;
    }

    // ----- Menu (right-click GUI) -----

    @Override
    public Component getDisplayName() {
        // Reuses the block's name as the GUI title.
        return Component.translatable("block.create_expansion.water_strainer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WaterStrainerMenu(containerId, playerInventory, this, dataAccess);
    }

    // Called every tick on the server (wired up in WaterStrainerBlock#getTicker).
    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterStrainerBlockEntity be) {
        // Only works when enough surrounding water is present.
        if (!hasEnoughWater(level, pos)) {
            be.progress = 0;
            return;
        }

        be.progress++;
        if (be.progress >= processingTicks()) {
            be.progress = 0;
            be.produceLoot((ServerLevel) level);
            be.setChanged();
        }
    }

    /** Ticks per production cycle, read live from the server config. */
    private static int processingTicks() {
        return Mod_Config.WATER_STRAINER_PROCESSING_TICKS.get();
    }

    // Rolls the loot table once and inserts whatever it produced.
    // Anything that doesn't fit (inventory full) is simply discarded.
    private void produceLoot(ServerLevel level) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(LOOT_TABLE);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        for (ItemStack stack : lootTable.getRandomItems(params)) {
            ItemHandlerHelper.insertItem(inventory, stack, false);
        }
    }

    // True when at least REQUIRED_WATER_NEIGHBOURS of the 4 horizontal neighbours contain water.
    private static boolean hasEnoughWater(Level level, BlockPos pos) {
        int waterNeighbours = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                waterNeighbours++;
                if (waterNeighbours >= REQUIRED_WATER_NEIGHBOURS) {
                    return true;
                }
            }
        }
        return false;
    }

    // ----- Saving / loading -----

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        progress = tag.getInt("Progress");
    }
}
