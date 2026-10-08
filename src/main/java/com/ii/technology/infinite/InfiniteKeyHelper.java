package com.ii.technology.infinite;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared lookup for keys served by II Technology's infinite cell.
 */
public final class InfiniteKeyHelper {
    private InfiniteKeyHelper() {
    }

    public static boolean isIITechnologyInfinite(AEKey key) {
        if (!(key instanceof AEItemKey itemKey)) return false;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemKey.getItem());
        if (id == null) return false;

        String token = id.toString();
        for (InfiniteKinds.Kind kind : InfiniteKinds.all()) {
            if (kind.items().contains(token)) return true;
        }
        return false;
    }
}
