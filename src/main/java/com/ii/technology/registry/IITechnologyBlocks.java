package com.ii.technology.registry;

import appeng.blockentity.AEBaseBlockEntity;
import com.ii.technology.IITechnology;
import com.ii.technology.cpu.TwoICpuBlock;
import com.ii.technology.cpu.TwoICpuBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block and block-item registration for II Technology. */
public final class IITechnologyBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IITechnology.ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IITechnology.ID);

    public static final DeferredBlock<TwoICpuBlock> TWO_I_CPU = BLOCKS.registerBlock(
            "2icpu",
            TwoICpuBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops());

    public static final DeferredItem<BlockItem> TWO_I_CPU_ITEM =
            ITEMS.registerSimpleBlockItem("2icpu", TWO_I_CPU);

    private IITechnologyBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TWO_I_CPU.get().setBlockEntity(
                    TwoICpuBlockEntity.class,
                    IITechnologyBlockEntities.TWO_I_CPU.get(),
                    null,
                    null);
            AEBaseBlockEntity.registerBlockEntityItem(
                    IITechnologyBlockEntities.TWO_I_CPU.get(),
                    TWO_I_CPU_ITEM.get());
        });
    }
}
