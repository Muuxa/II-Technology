package com.ii.technology.mixin.ae2;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Lets the split controller build extra CPU views from the same block entity. */
@Mixin(value = CraftingCPUCluster.class, remap = false)
public interface CraftingCPUClusterAccessor {
    @Invoker("addBlockEntity")
    void iitech$addBlockEntity(CraftingBlockEntity blockEntity);

    @Invoker("done")
    void iitech$done();
}
