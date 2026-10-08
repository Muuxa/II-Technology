package com.ii.technology.client.mixin.aurelium;

import appeng.api.stacks.AEKey;
import com.ii.technology.infinite.InfiniteKeyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets AURELIUM's terminal renderer treat II Technology keys as infinite without adding an
 * II Technology kind to AURELIUM's creative-tab registry.
 */
@Mixin(targets = "com.muuxa.aurelium.client.ClientInfiniteKeys", remap = false)
public abstract class ClientInfiniteKeysMixin {
    @Inject(method = "isInfinite", at = @At("RETURN"), cancellable = true, require = 0)
    private static void iitech$alsoOurInfiniteKeys(AEKey key,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && InfiniteKeyHelper.isIITechnologyInfinite(key)) {
            cir.setReturnValue(true);
        }
    }
}
