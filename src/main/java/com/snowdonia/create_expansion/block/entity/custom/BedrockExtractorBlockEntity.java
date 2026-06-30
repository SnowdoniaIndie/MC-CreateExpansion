package com.snowdonia.create_expansion.block.entity.custom;

import com.snowdonia.create_expansion.CreateExpansion;
import com.snowdonia.create_expansion.block.entity.Mod_BlockEntities;
import com.snowdonia.create_expansion.screen.custom.BedrockExtractorMenu;
import com.snowdonia.create_expansion.util.ExtractOnlyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BedrockExtractorBlockEntity extends BlockEntity implements MenuProvider {

    /** Number of inventory slots. */
    public static final int INVENTORY_SIZE = 9;
    /** 20 ticks = 1 second, so 200 ticks = 10 seconds. */
    private static final int GENERATE_INTERVAL = 200;
    /** Loot table rolled to decide what the extractor produces. Lives at
     *  data/create_expansion/loot_table/gameplay/bedrock_extractor.json */
    private static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "gameplay/bedrock_extractor"));

    // The 9-slot inventory. setChanged() marks the BE dirty so it gets saved.
    private final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // The view exposed to hoppers/pipes: extract allowed, insert never.
    private final ExtractOnlyItemHandler extractOnlyInventory = new ExtractOnlyItemHandler(inventory);

    // Counts up each tick while active; resets after producing an item.
    private int progress = 0;

    // Syncs progress to the open menu/screen so the progress bar updates live.
    // index 0 = current progress, index 1 = the interval it's counting toward.
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> GENERATE_INTERVAL;
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

    public BedrockExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(Mod_BlockEntities.BEDROCK_EXTRACTOR_BE.get(), pos, blockState);
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
        return Component.translatable("block.create_expansion.bedrock_extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BedrockExtractorMenu(containerId, playerInventory, this, dataAccess);
    }

    // Called every tick on the server (wired up in BedrockExtractorBlock#getTicker).
    public static void serverTick(Level level, BlockPos pos, BlockState state, BedrockExtractorBlockEntity be) {
        // Only works when there is a block of bedrock directly below.
        if (!hasBedrockBelow(level, pos)) {
            be.progress = 0;
            return;
        }

        be.progress++;
        if (be.progress >= GENERATE_INTERVAL) {
            be.progress = 0;
            be.produceLoot((ServerLevel) level);
            be.setChanged();
        }
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

    // True when the block directly below is bedrock.
    private static boolean hasBedrockBelow(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.BEDROCK);
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
