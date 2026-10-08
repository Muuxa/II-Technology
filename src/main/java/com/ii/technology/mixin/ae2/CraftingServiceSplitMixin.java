package com.ii.technology.mixin.ae2;

import appeng.me.service.CraftingService;
import com.ii.technology.cpu.IITechnologyCpuSplits;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Adds split 2i CPU views after AE2 refreshes its normal crafting CPU list. */
@Mixin(value = CraftingService.class, remap = false)
public abstract class CraftingServiceSplitMixin {
    @Inject(method = "updateCPUClusters", at = @At("TAIL"))
    private void iitech$injectSplitCpus(CallbackInfo callbackInfo) {
        CraftingServiceAccessor accessor = (CraftingServiceAccessor) (Object) this;
        IITechnologyCpuSplits.updateService(accessor);
    }
}
