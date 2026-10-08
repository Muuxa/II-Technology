package com.ii.technology.registry;

import com.ii.technology.IITechnology;
import com.ii.technology.cpu.TwoICpuBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block entity registration for II Technology. */
public final class IITechnologyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IITechnology.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TwoICpuBlockEntity>> TWO_I_CPU =
            BLOCK_ENTITIES.register("2icpu", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new TwoICpuBlockEntity(IITechnologyBlockEntities.TWO_I_CPU.get(), pos, state),
                    IITechnologyBlocks.TWO_I_CPU.get()).build(null));

    private IITechnologyBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
