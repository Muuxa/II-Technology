package com.ii.technology.cpu;

import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.api.orientation.BlockOrientation;
import appeng.blockentity.crafting.CraftingBlockEntity;
import com.ii.technology.cpu.IITechnologyCpuPayloads.OpenCpuEditorPayload;
import com.ii.technology.registry.IITechnologyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.Set;

/**
 * A pool-style AE2 crafting CPU. The configured storage/parallel values are the total pool;
 * crafting submissions automatically carve out per-job slices without a manual split count.
 */
public final class TwoICpuBlockEntity extends CraftingBlockEntity {
    public static final long MIN_PARALLEL = 1L;
    public static final long MAX_PARALLEL = 1L;
    public static final long MIN_STORAGE_BYTES = 1L;
    public static final long MAX_STORAGE_BYTES = Long.MAX_VALUE;

    private long parallel = 1L;
    private long storageBytes = TwoICpuType.DEFAULT_STORAGE_BYTES;
    private long reservedParallel;
    private long reservedStorageBytes;

    private transient long constructionStorageOverride = -1L;
    private transient int constructionParallelOverride = -1;

    public TwoICpuBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onReady() {
        if (getCustomName() == null) {
            setName("2i CPU");
        }
        super.onReady();
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public long getStorageBytes() {
        if (constructionStorageOverride >= 0L) {
            return Math.max(1L, constructionStorageOverride);
        }
        return Math.max(1L, storageBytes - reservedStorageBytes);
    }

    @Override
    public int getAcceleratorThreads() {
        if (constructionParallelOverride >= 0) {
            return constructionParallelOverride;
        }
        return saturatedInt(parallel - reservedParallel);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        return IITechnologyBlocks.TWO_I_CPU_ITEM.get();
    }

    public long getTotalParallel() {
        return parallel;
    }

    public long getConfiguredStorageBytes() {
        return storageBytes;
    }

    public long getReservedParallel() {
        return reservedParallel;
    }

    public long getReservedStorageBytes() {
        return reservedStorageBytes;
    }

    public long getRemainingParallel() {
        return Math.max(0L, parallel - reservedParallel);
    }

    public long getRemainingStorageBytes() {
        return Math.max(0L, storageBytes - reservedStorageBytes);
    }

    public void setParallel(long value) {
        parallel = 1L;
        applyConfigurationChange();
    }

    public void setStorageBytes(long value) {
        storageBytes = clamp(value, MIN_STORAGE_BYTES, MAX_STORAGE_BYTES);
        applyConfigurationChange();
    }

    public void reserve(long storage, long parallelThreads) {
        reservedStorageBytes = Math.min(storageBytes, reservedStorageBytes + Math.max(0L, storage));
        reservedParallel = Math.min(parallel, reservedParallel + Math.max(0L, parallelThreads));
    }

    public void release(long storage, long parallelThreads) {
        reservedStorageBytes = Math.max(0L, reservedStorageBytes - Math.max(0L, storage));
        reservedParallel = Math.max(0L, reservedParallel - Math.max(0L, parallelThreads));
    }

    public void setConstructionOverride(long storage, int parallelThreads) {
        constructionStorageOverride = storage;
        constructionParallelOverride = Math.max(0, parallelThreads);
    }

    public void clearConstructionOverride() {
        constructionStorageOverride = -1L;
        constructionParallelOverride = -1;
    }

    private void applyConfigurationChange() {
        IITechnologyCpuSplits.onCpuConfigurationChanged(this);
    }

    public void rebuildPoolCluster() {
        if (getCluster() != null) {
            getCluster().breakCluster();
        }
        saveChanges();
        if (level != null) {
            updateMultiBlock(worldPosition);
            markForUpdate();
            var node = getMainNode().getNode();
            if (node != null && node.getGrid() != null) {
                node.getGrid().postEvent(new GridCraftingCpuChange(node));
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("ii_parallel", parallel);
        tag.putLong("ii_storage_bytes", storageBytes);
    }

    @Override
    public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadTag(tag, registries);
        if (tag.contains("ii_parallel")) {
            parallel = clamp(tag.getLong("ii_parallel"), MIN_PARALLEL, MAX_PARALLEL);
        }
        if (tag.contains("ii_storage_bytes")) {
            storageBytes = clamp(tag.getLong("ii_storage_bytes"), MIN_STORAGE_BYTES, MAX_STORAGE_BYTES);
        }
    }

    public void openEditor(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new OpenCpuEditorPayload(worldPosition, storageBytes));
        }
    }

    private static int saturatedInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }
}
