package com.ii.technology.mixin.ae2;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.security.IActionSource;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.ii.technology.cpu.IITechnologyCpuSplits;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Intercepts submissions to a 2i CPU pool and carves out a per-job storage slice. */
@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class CraftingCPUClusterSubmitMixin {
    @Inject(method = "submitJob", at = @At("HEAD"), cancellable = true)
    private void iitech$autoSliceOnSubmit(IGrid grid, ICraftingPlan plan, IActionSource source,
                                          ICraftingRequester requester,
                                          CallbackInfoReturnable<ICraftingSubmitResult> cir) {
        ICraftingSubmitResult result = IITechnologyCpuSplits.trySubmitSplit(
                (CraftingCPUCluster) (Object) this, grid, plan, source, requester);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
