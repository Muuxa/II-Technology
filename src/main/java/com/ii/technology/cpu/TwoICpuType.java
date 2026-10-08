package com.ii.technology.cpu;

import appeng.block.crafting.ICraftingUnitType;
import com.ii.technology.registry.IITechnologyBlocks;
import net.minecraft.world.item.Item;

/** AE2-facing type for the configurable 2i crafting CPU block. */
public enum TwoICpuType implements ICraftingUnitType {
    INSTANCE;

    public static final long DEFAULT_STORAGE_BYTES = 1024L;
    public static final int DEFAULT_PARALLEL = 1;

    @Override
    public long getStorageBytes() {
        return DEFAULT_STORAGE_BYTES;
    }

    @Override
    public int getAcceleratorThreads() {
        return DEFAULT_PARALLEL;
    }

    @Override
    public Item getItemFromType() {
        return IITechnologyBlocks.TWO_I_CPU_ITEM.get();
    }
}
