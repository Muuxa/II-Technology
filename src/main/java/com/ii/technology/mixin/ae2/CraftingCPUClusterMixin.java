package com.ii.technology.mixin.ae2;

import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Raises AE2's per-block co-processor limit so 2i CPU blocks can carry configurable parallelism. */
@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class CraftingCPUClusterMixin {
    @ModifyConstant(method = "addBlockEntity", constant = @Constant(intValue = 16), require = 0)
    private int iitech$raisePerBlockLimit(int original) {
        return Integer.MAX_VALUE;
    }
}
