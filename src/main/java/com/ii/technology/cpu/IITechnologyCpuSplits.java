package com.ii.technology.cpu;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.security.IActionSource;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.ii.technology.mixin.ae2.CraftingCPUClusterAccessor;
import com.ii.technology.mixin.ae2.CraftingServiceAccessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Auto-slicing pool for 2i CPUs. Each crafting submission reserves the bytes it needs and gets its
 * own temporary CPU view; the base CPU keeps the remaining storage for later submissions.
 */
public final class IITechnologyCpuSplits {
    private static final Map<CraftingCPUCluster, JobSlice> ACTIVE_SLICES = new HashMap<>();
    private static boolean rebuilding;

    private IITechnologyCpuSplits() {
    }

    public static void updateService(CraftingServiceAccessor accessor) {
        Set<CraftingCPUCluster> clusters = accessor.iitech$getCpuClusters();
        releaseFinished(accessor);
        for (TwoICpuBlockEntity cpu : accessor.iitech$getGrid().getMachines(TwoICpuBlockEntity.class)) {
            CraftingCPUCluster base = cpu.getCluster();
            if (base != null) {
                clusters.add(base);
            }
        }
        clusters.addAll(ACTIVE_SLICES.keySet());
    }

    public static ICraftingSubmitResult trySubmitSplit(CraftingCPUCluster pool, IGrid grid,
                                                       ICraftingPlan plan, IActionSource source,
                                                       ICraftingRequester requester) {
        if (ACTIVE_SLICES.containsKey(pool)) return null;
        TwoICpuBlockEntity cpu = findCpu(pool);
        if (cpu == null) return null;

        long required = Math.max(1L, plan.bytes());
        long remainingStorage = cpu.getRemainingStorageBytes();
        if (required > remainingStorage) return null;

        long remainingParallel = cpu.getRemainingParallel();
        long allocateParallel = remainingParallel <= 0L ? 0L : Math.min(1L, remainingParallel);
        cpu.reserve(required, allocateParallel);

        CraftingCPUCluster slice = createSlice(cpu, required,
                (int) Math.min(Integer.MAX_VALUE, allocateParallel));
        ACTIVE_SLICES.put(slice, new JobSlice(cpu, required, allocateParallel));
        rebuildPool(cpu);
        return slice.submitJob(grid, plan, source, requester);
    }

    public static void onCpuConfigurationChanged(TwoICpuBlockEntity cpu) {
        ACTIVE_SLICES.entrySet().removeIf(entry -> {
            JobSlice job = entry.getValue();
            if (job.cpu() != cpu) return false;
            cpu.release(job.storageBytes(), job.parallelThreads());
            return true;
        });
        rebuildPool(cpu);
    }

    private static CraftingCPUCluster createSlice(TwoICpuBlockEntity cpu, long storage, int parallel) {
        CraftingCPUCluster slice = new CraftingCPUCluster(cpu.getBlockPos(), cpu.getBlockPos());
        cpu.setConstructionOverride(storage, parallel);
        try {
            ((CraftingCPUClusterAccessor) (Object) slice).iitech$addBlockEntity(cpu);
            ((CraftingCPUClusterAccessor) (Object) slice).iitech$done();
        } finally {
            cpu.clearConstructionOverride();
        }
        return slice;
    }

    private static void releaseFinished(CraftingServiceAccessor accessor) {
        Iterator<Map.Entry<CraftingCPUCluster, JobSlice>> iterator = ACTIVE_SLICES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CraftingCPUCluster, JobSlice> entry = iterator.next();
            CraftingCPUCluster slice = entry.getKey();
            if (!slice.isDestroyed() && slice.isBusy()) continue;

            JobSlice job = entry.getValue();
            job.cpu().release(job.storageBytes(), job.parallelThreads());
            accessor.iitech$getCpuClusters().remove(slice);
            iterator.remove();
            rebuildPool(job.cpu());
        }
    }

    private static void rebuildPool(TwoICpuBlockEntity cpu) {
        if (rebuilding) return;
        rebuilding = true;
        try {
            cpu.rebuildPoolCluster();
        } finally {
            rebuilding = false;
        }
    }

    private static TwoICpuBlockEntity findCpu(CraftingCPUCluster cluster) {
        Iterator<CraftingBlockEntity> iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof TwoICpuBlockEntity cpu) {
                return cpu;
            }
        }
        return null;
    }

    private record JobSlice(TwoICpuBlockEntity cpu, long storageBytes, long parallelThreads) {
    }
}
