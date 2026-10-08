package com.ii.technology.mixin.ae2;

import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.service.CraftingService;
import appeng.api.networking.IGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/** Access to AE2's live crafting CPU set. */
@Mixin(value = CraftingService.class, remap = false)
public interface CraftingServiceAccessor {
    @Accessor("craftingCPUClusters")
    Set<CraftingCPUCluster> iitech$getCpuClusters();

    @Accessor("grid")
    IGrid iitech$getGrid();
}
